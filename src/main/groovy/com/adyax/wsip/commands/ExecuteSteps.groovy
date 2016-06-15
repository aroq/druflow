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

    def stepsToExecute = []

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

    def executeStep(step) {
        def result
        step.execute()
        result
    }

    def perform() {
        def runsCount = 0
        def result = []
        contextScripts.each { contextScript ->
            stepsToExecute << contextScript.execute(stage, runs, site)
        }

        def steps = [:]

        runs.each { run ->
            steps[run] = []
            runsCount++
            stepsToExecute.each { script ->
                steps[run] << script.findAll {
                    it.key == run
                }
                .collect {
                    it.value
                }
            }

            steps[run] = steps[run].flatten()

            steps[run].each { step ->
                if (step.overrideCheck && (step.overrideCheck instanceof Closure || step.overrideCheck.type.name == 'groovy.lang.Closure')) {
                    step.overrideCheck.delegate = this
                    step.overrideCheck.resolveStrategy = Closure.DELEGATE_FIRST
                    step.override = step.overrideCheck.call(config.env)
                }
                if (step.override.toBoolean()) {
                    log "Overriding other steps as per stage config."
                    steps[run] = [step]
                }
            }

            steps[run].each { step ->
                debug("Run #${runsCount}: environment: ${run}, stage: ${stage}")
                result << executeStep(step)
            }
        }

        result
    }

}
