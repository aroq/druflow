/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip

import com.adyax.wsip.commands.Command

@Singleton
class Logger {

    def out = null

    def caller

    def executeCount = 0

    def registerOut(out) {
        this.out = out
    }

    def _log(message, c = null) {
        def oldCaller = null
        if (c) {
            oldCaller = caller
            caller = c
        }
        if (caller) {
            if (caller instanceof Command) {
            }
            else {
                message += " (${caller.getClass()})"
            }
            if (Config.instance.config.debug) {
                message = '|  ' * executeCount + "|-- " + message
            }
        }
        if (out) {
            out.println(message)
        }
        else {
            println message
        }
        if (c) {
            caller = oldCaller
        }
    }

    def log(message, c = null) {
        if (!Config.instance.config.stopLogging) {
            if (Config.instance.config.debug) {
                message = "LOG - " + message
            }
            _log(message, c)
        }
    }

    def debug(message, c = null) {
        if (Config.instance.config.debug) {
            _log("DEBUG - ${message}", c)
        }
    }

    def error(message, c = null) {
        _log("ERROR - ${message}", c)
    }
}
