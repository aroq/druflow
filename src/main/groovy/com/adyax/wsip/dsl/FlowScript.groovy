/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.dsl

import com.adyax.wsip.Logger
import com.adyax.wsip.utils.Common

abstract class FlowScript extends Script {

    @Delegate Common commonUtils = Common.instance
    @Delegate Logger logger = Logger.instance

    def stage(stage, closure = null) {
        this.binding.flow.stage(stage, closure)
    }

    def siteList(params) {
        this.binding.flow.siteList(params)
    }

    def methodMissing(String name, parameters) {
        def context
        if (!context) {
            debug "No context defined for: ${name}, using Default context"
            context = retrieveContext()
        }
        def params = [:]
        // TODO: Handle parameters properly.
        if (parameters && parameters[0]) {
            if (parameters[0] instanceof String) {
                params['command'] = parameters[0]
            } else {
                parameters[0].each { param ->
                    params[param.key] = param.value
                }
            }
            ['site', 'context'].each { p ->
                if (this.hasProperty(p)) {
                    params[p] = this."${p}"
                }
            }
        }
        try {
            Common.instance._executeCommand(name, this, params)
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
        }
    }
}

