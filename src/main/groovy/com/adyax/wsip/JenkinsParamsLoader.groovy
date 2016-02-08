/**
 * Created by alex on 26.06.15.
 */

package com.adyax.wsip

class JenkinsParamsLoader {

    def loadParams(command, params) {
        // Get the current build parameters.
        Config.instance.config.build.buildVariables.each { param ->
            if (!params.containsKey(param.key)) {
                params.put(param.key, param.value)
            }
        }

        if (!params.containsKey('workspace')) {
            params.put('workspace', Config.instance.config.build.workspace)
        }
    }
}
