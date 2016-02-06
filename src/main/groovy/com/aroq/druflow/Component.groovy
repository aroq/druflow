package com.aroq.druflow

class Component {
    def config

    def componentName

    def componentConfig

    def scriptObject

    def log(message) {
        if (config.environment == 'jenkins') {
            scriptObject.out.println message
        }
        else {
            println message
        }
    }

    def getBranchListByPattern(pattern) {

        def gitlabBranches = new groovy.json.JsonSlurper().parseText(new URL("${config.gitlabAddress}/api/v3/projects/${config.projectID}/repository/branches?private_token=${config.privateToken}").text).name

        gitlabBranches.findAll { it.value =~ pattern }
    }

    def prepareBranch(name, branch, parent) {
        def branches = []
        def autoMergeToParent = false
        if (branch.autoMergeToParent && branch.autoMergeToParent != null) {
            autoMergeToParent = true
        }
        def newBranch = new Branch(name: name, parent: parent, autoMergeToParent: autoMergeToParent)
        branches << newBranch
        if (branch.branches) {
            branches += processBranchWorkflow(branch.branches, newBranch)
        }
        branches
    }

    def processNamePattern(pattern, parent) {
        pattern.findAll(/\{([^\}]*)\}/).each {
            def matcher = it =~ /\{(.*)\[(.*)\]\}/
            if (matcher.matches()) {
                def paramName = matcher[0][1]
                def paramPart = matcher[0][2]
                if (paramName == 'parentName') {
                    def paramValue = parent.name.split('/')[paramPart.toInteger()]
                    pattern = pattern.replace(it, paramValue)
                }
            }
        }

        pattern
    }

    def processBranchWorkflow(processedBranches, parent = null) {
        def branches = []
        processedBranches.each { name, branch ->
            if (branch.namePattern) {
                def pattern = processNamePattern(branch.namePattern, parent)
                getBranchListByPattern(pattern)?.each{
                    def mergeToBranch = it - "remotes/${componentConfig.remoteName}/"
                    branches += prepareBranch(mergeToBranch, branch, parent)
                }
            }
            else {
                branches += prepareBranch(name, branch, parent)
            }
        }
        branches
    }

    def loadConfig() {
        if (config.environment == 'jenkins') {
            return scriptObject.readFileFromWorkspace("config/${componentName}.groovy")
        }
        else {
            def configFile = new File(rootDir(), "config/${componentName}.groovy")
            if (configFile.exists()) {
               return configFile.text
            }
        }
    }

    def rootDir() {
        def dir = new File(System.getProperty("user.dir"))
        if (dir.exists()) {
            return dir.getPath()
        }
    }

    def processComponent() {
        def componentConfigFile = loadConfig()

        def slurper = new ConfigSlurper()

        componentConfig = slurper.parse(componentConfigFile)

        scriptObject.folder("${componentConfig.projectFolder}/${componentName}")

        def branches = processBranchWorkflow(componentConfig.branchWorkflow.branches)

        branches.unique { branch ->
            branch.name + branch.parentName
        }

        branches.each {
            if (it.name && it.parentName) {
                createJob(mergeFromBranch: it.parentName, mergeToBranch: it.name)
                if (it.autoMergeToParent) {
                    createJob(mergeFromBranch: it.name, mergeToBranch: it.parentName)
                }
            }
        }
    }

    def createJob(params) {
        params.mergeFromBranch = params.mergeFromBranch.trim()
        params.mergeToBranch = params.mergeToBranch.trim()

        log "${params.mergeFromBranch} -> ${params.mergeToBranch}"

        def mergeToBranchNameProcessed = params.mergeToBranch.replaceAll('/', '-').replaceAll("\\*", 'all')
        def mergeFromBranchNameProcessed = params.mergeFromBranch.replaceAll('/', '-').replaceAll("\\s", '').replaceAll("\\*", 'all')

        scriptObject.job("${componentConfig.projectFolder}/${componentName}/${componentConfig.jobPrefix}${componentName}-${mergeFromBranchNameProcessed}-to-${mergeToBranchNameProcessed}") {
            scm {
                git {
                    remote {
                        name(componentConfig.remoteName)
                        url(componentConfig.gitURL)
                    }
                    branch("${componentConfig.remoteName}/${params.mergeFromBranch}")
                    mergeOptions {
                        // Sets the name of the branch to merge.
                        branch(params.mergeToBranch.trim())
                        // Sets the name of the repository that contains the branch to merge.
                        remote(componentConfig.remoteName)
                        // Selects the merge strategy.
                        strategy('default')
                    }
                }
                triggers {
                    scm('@hourly')
                }
            }

            publishers {
                git {
                    pushOnlyIfSuccess()
                    pushMerge(true)
                }
            }
        }
    }
}

