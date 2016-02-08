package com.adyax.wsip

import com.adyax.wsip.utils.Common

/**
 * Created by alex on 03.08.15.
 */
class Base {

    Context context

    def config = Config.instance.config

    @Delegate Logger logger = Logger.instance
    @Delegate Common commonUtils = Common.instance

    def params = [:]

    def executeCommand(commandName, params = [:]) {
        _executeCommand(commandName, this, params)
    }

    def methodMissing(String name, parameters) {
        if (!context) {
            debug "No context defined for: ${name}, using Default context"
            context = retrieveContext()
        }
        def params = [:]
        parameters.each {
            if (it instanceof String) {
                params['command'] = it
            } else {
                it.each { param ->
                    params[param.key] = param.value
                }
            }
        }
        ['site', 'context'].each { p ->
            if (this.hasProperty(p) && !params[p]) {
                params[p] = this."${p}"
            }
        }
        try {
            if (context.commandAllowed(name, params)) {
                executeCommand(name, params)
            }
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
            throw e
        }
    }

}
