/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.ContextScript

class ExecuteContextCommand extends Command {

    def script

    def perform() {
        def context = retrieveContext('project')
        context.checkSites = true
        def contextScript = new ContextScript(script: script, context: context)
        contextScript.execute('main', ['all'])
    }

}
