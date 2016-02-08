/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ExecuteSteps extends Command {

    String site

    String stage

    def contextScripts

    def runs

    Boolean reverse = false

    def transformParams() {
        if (contextScripts instanceof Closure) {
            def closure = contextScripts
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            contextScripts = closure.call()
        }
        if (reverse) {
            contextScripts = contextScripts.reverse()
        }
    }

    def perform() {
        def result = []
        contextScripts.each { contextScript ->
            result << contextScript.execute(stage, runs, site)
        }
        result
    }

}
