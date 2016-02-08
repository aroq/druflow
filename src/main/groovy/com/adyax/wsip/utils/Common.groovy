package com.adyax.wsip.utils

import com.adyax.wsip.Config
import com.adyax.wsip.Logger
import groovy.io.FileType
import groovy.json.JsonSlurper

@Singleton
class Common {

    @Delegate Logger logger = Logger.instance

    def config = Config.instance.config

    def _executeCommand(commandName, caller, params = [:]) {
        if (config.commands.containsKey(commandName)) {
            def command = config.commands[commandName]
            command['caller'] = caller
            command['command'] = commandName
            if (params) {
                command += params
            }
            // TODO: Refactor below code.
            def className = getClass().classLoader.loadClass(command['className']).name
            def metaClassProperties = getClass().classLoader.loadClass(command['className']).metaClass.properties

            if (!metaClassProperties.find { it.name == 'site'}) {
                command.remove('site')
            }
            getClass().classLoader.loadClass(command['className'])?.newInstance(command)?.execute()
        }
        else {
            error("Command not found: ${commandName}");
        }
    }

    def rootDir() {
        // TODO: Find more elegant way to handle compiled class execution case.

        if (config.environment == 'jenkins') {
            return config.build.workspace.toString() + '/ops'
        }

        System.getProperty("user.dir")

        // Check if script is located in working dir.
//        def appFile = new File(System.getProperty("user.dir") + '/app.groovy')
////        if (appFile.exists()) {
//            return appFile.getParentFile().getParentFile().getPath()
//        }
//        else {
//            return new File(getClass().protectionDomain.codeSource.location.path).getParentFile().getParentFile().getPath()
//        }
    }

    def scriptsDir() {
        config.rootDir + '/' + 'scripts'
    }

    def scriptsDirFile(fileName) {
        new File(scriptsDir() + '/' + fileName)
    }

    def workspace() {
        config.workspace ? config.workspace : new File(rootDir()).getParentFile().getPath()
    }

    def docrootDir(ws = null) {
        ws = ws ? ws : workspace()
        ws + '/' + config.docrootDir
    }

    def docrootPrefixDir(ws = null) {
        docrootDir(ws) + '/' + config.docrootPrefixDir
    }

    def docrootConfigDir() {
        docrootDir() + '/config'
    }

    def docrootTmpDir() {
        new File(docrootDir() + '/tmp')
    }

    def tmpDir() {
        def dir = new File(workspace() + '/tmp')
        dir.mkdirs()
        dir
    }

    def drupalDir(String ws = null) {
        def fileName = docrootPrefixDir(ws) + '/' + config.drupalDir
        new File(fileName)
    }

    def sitesDir() {
        docrootPrefixDir() + '/' + config.sitesDir
    }

    File siteDir(site, sitesDir = sitesDir()) {
        new File(sitesDir + '/' + site)
    }

    def projectsDir() {
        docrootPrefixDir() + '/' + config.projectsDir
    }

    def projectGitDir(projectName) {
        if (config.projectGitDir) {
            config.projectGitDir
        }
        else {
            projectDir(projectName)
        }
    }

    def projectDir(projectName = null) {
        def result = ''
        new File(docrootConfigDir()).eachFileRecurse(FileType.DIRECTORIES) {
            if (it.name == projectName) {
                 result = it
            }
        }
        if (result) {
            return docrootDir() + (result.toString() - docrootConfigDir())
        }
        else return ''
    }

    def sitesDirFile(fileName) {
        new File(sitesDir() + '/' + fileName)
    }

    def transformProjectName(name) {
        def transitionsFile = sitesDirFile('transitions.json')
        if (transitionsFile.exists()) {
            JsonSlurper.newInstance().parseText(transitionsFile.text).each { param ->
                if (name?.contains(param.source)) {
                    def oldName = name
                    name = name.replaceAll(param.source, param.target)
                    debug "Transition of project name: ${oldName} to ${name}"
                }
            }
        }
        name
    }

    def projectNameVariants(name) {
        LinkedHashMap variants = [:]
        ArrayList groups = [:]
        name = name.replaceAll(/[._-]/, '.')
        ArrayList nameParts = name.tokenize('.')

        File transitionsFile = sitesDirFile('transitions.json')
        def params = []
        if (transitionsFile.exists()) {
            params = JsonSlurper.newInstance().parseText(transitionsFile.text)
        }

        nameParts.each { namePart ->
            variants[namePart] = [namePart]
            params.each { param ->
                if (namePart?.contains(param.target )) {
                    variants[namePart] << namePart.replaceAll(param.target, param.source)
                }
            }
        }

        Integer level = 0
        variants.each { key, group ->
            level++
            groups[level] = []
            group.each { part ->
                if (level > 1) {
                    groups[level - 1].each { variant ->
                        ['.', '-', '_'].each { delimiter ->
                            groups[level] << variant + delimiter + part
                        }
                    }
                }
                else {
                    groups[level] << part
                }
            }
        }
        def agencies
        File agenciesFile = sitesDirFile('agencies.json')
        if (agenciesFile.exists()) {
            agencies = JsonSlurper.newInstance().parseText(agenciesFile.text)
            assert agencies
        }

        ArrayList result = []
        groups.each { group ->
            group.each { item ->
                result << item
                agencies.each { agency ->
                    assert agency['name']
                    ['.', '-', '_'].each { delimiter ->
                        result << item + delimiter + agency['name']
                    }
                }
            }
        }

        result
    }

    def escapedName(name) {
        name.replaceAll(/[._-]/, /[._-]/)
    }

    def retrieveContext(String name = null) {
        if (name) {
            config.contexts[name] ? config.contexts[name] : null
        }
        else {
            retrieveContext('defaultContext')
        }
    }

    def executeInTempDirAndPushToGit(LinkedHashMap params, closure) {
        def tmp = docrootTmpDir()
        tmp.mkdirs()

        def dir = _executeCommand('gitGetRepo', params.caller, [repoDirName: params.repoDirName, dir: tmp, branch: params.branch])
        def result
        try {
            result = closure.call(dir)
        }
        finally {
            if (params.final instanceof Closure) {
                params.final.call(dir)
            }
            _executeCommand('gitPushRepo', params.caller, [repoDir: dir, branch: params.branch, message: params.message])
        }


        result
    }

    def executeInTempDir(LinkedHashMap params, closure) {
        def tmp = new File(tmpDir(), params.subDir)
        tmp.mkdirs()
        def result
        try {
            result = closure.call(tmp)
        }
        finally {
            _executeCommand('shellCommand', params.caller, [cmd: "rm -fR ${params.subDir}", dir: tmpDir()])
        }
        result
    }

    def executeInTempGitDir(LinkedHashMap params, closure) {
        def result
        executeInTempDir(subDir: 'repos', caller: params.caller) { tmp ->
            params.dir = tmp
            def gitDir = _executeCommand('gitGetRepo', params.caller, params)
            result = closure.call(gitDir)
        }
        result
    }

}
