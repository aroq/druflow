/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

abstract class CopyCommand extends Command {

    def transformParams() {
        super.transformParams()
        if (!toEnv) {
           toEnv = env
        }

        if (!toSite) {
            toSite = site
        }

        if (!to) {
            to = from
        }
    }

}
