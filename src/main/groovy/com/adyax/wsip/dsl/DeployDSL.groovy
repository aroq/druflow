package com.adyax.wsip.dsl

import com.adyax.wsip.Step

/**
 * Created by alex on 02.08.15.
 */
class DeployDSL extends BaseDSL {

    String run

    String site

    String script

    def stepsToExecute = []

    def env(env, closure) {
        if (run == env) {
            this.env = env
            executeClosure(closure)
        }
    }

    def stage(String stepStage, Closure closure) {
        if (stepStage == "all" || this.stage == stepStage) {
            stepsToExecute << new Step(context: context, closure: closure, env: env, stage: stepStage, site: site, script: script)
        }
    }

    def stage(HashMap params, String stepStage, Closure closure) {
        if (stepStage == "all" || this.stage == stepStage) {
            def defaultParams = [context: context, closure: closure, env: env, stage: stepStage, site: site, override: false, script: script]
            defaultParams << params
            stepsToExecute << new Step(defaultParams << params)
        }
    }

}
