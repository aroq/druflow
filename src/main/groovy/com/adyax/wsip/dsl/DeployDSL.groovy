package com.adyax.wsip.dsl

/**
 * Created by alex on 02.08.15.
 */
class DeployDSL extends BaseDSL {

    String run

    String site

    def env(env, closure) {
        if (run == env) {
            this.env = env
            executeClosure(closure)
        }
    }

    def stage(stepStage, closure) {
        if (stepStage == "all" || this.stage == stepStage) {
            log "[Context: ${context.name}] [Environment: ${env}] [Stage: ${stepStage}] [Site: ${site}]"
            executeClosure(closure)
        }
    }

}
