package com.adyax.wsip.dsl
/**
 * Created by alex on 02.08.15.
 */
class FlowDSL extends BaseDSL {

    String flowName

    def stage(stage, closure = null) {
        debug("Executing stage: ${stage}")
        this.stage = stage
        executeClosure(closure)
    }

}
