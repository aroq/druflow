package com.adyax.wsip

import com.adyax.wsip.commands.*

class Application extends Command {

    def requiredConfigParams

    def loadParams() {
        super.loadParams()

        def params = [:]

        // Load params depending on environment, from config path for local and non-system Jenkins scripts.
        getClass().classLoader.loadClass(config.paramsLoader)?.newInstance()?.loadParams(command, params)
        // Override existing config values.
        Config.instance.addParams(params, true)

        // Load params from docroot config
        // TODO: Consider to move it info config.
        def docrootConfigFile = new File(docrootConfigDir() + '/docroot.config')
        if (docrootConfigFile.exists()) {
            Config.instance.addParams(ConfigSlurper.newInstance(config.environment).parse(docrootConfigFile.text), true)
        }
        else {
            error "Docroot config file not found: ${docrootConfigFile}"
        }

        // Process Jenkins strings into boolean.
        // TODO: Refactor it.
        if (config.simulate == '0') {
            config.simulate = false
        }
        if (config.debug == '0') {
            config.debug = false
        }
        if (config.force == '0') {
            config.force = false
        }

        // Process context params and add params to config.
        config.contexts = [:]
        config.contextParams.each { String contextName, context ->
            config.contexts[contextName] = new Context(context)
        }

        debug "Environment: ${config.environment}"
        debug "Groovy version: ${GroovySystem.version}"
        debug "Current dir: ${System.getProperty("user.dir")}"
    }

    def perform() {
        executeCommand(config.executeCommand)
    }

}