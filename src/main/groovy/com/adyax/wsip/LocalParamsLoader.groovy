/**
 * Created by alex on 26.06.15.
 */

package com.adyax.wsip

import groovy.json.JsonSlurper

class LocalParamsLoader {

    def loadParams(commandName, params) {
        // TODO: provide proper working dir.
        [
            new File("${Config.instance.config.configPath}")
        ].each { file ->
            if (file.exists()) {
                def extension = (file.name =~ /.[a-z]*/)[-1]
                def parameters
                if (extension == '.json') {
                    parameters = JsonSlurper.newInstance().parseText(file.text)
                }
                else {
                    parameters = ConfigSlurper.newInstance().parse(file.text)
                }

                parameters.each { param ->
                    if (!params.containsKey(param.key)) {
                        params.put(param.key, param.value)
                    }
                }
            }
        }
    }
}
