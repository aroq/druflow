/**
 * Created by alex on 26.06.15.
 */

package com.adyax.wsip

@Singleton
class Config {

    def config = [:]

    def addParams(parameters, overwrite = false) {
        parameters.each() { param, value ->
            if (overwrite || !config.containsKey(param)) {
                config[param] = value
            }
        }
    }
}
