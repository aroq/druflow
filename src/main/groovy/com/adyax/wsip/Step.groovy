package com.adyax.wsip

import com.adyax.wsip.commands.Command

/**
 * Created by Aroq on 17/02/16.
 */
class Step extends Command {

    Closure closure

    String env

    String stage

    String site

    Boolean override = false

    Closure overrideCheck

    Context context

    String script

    def perform() {
        def result
        log "EXECUTE STEP: ${script}: [Context: ${context.name}] [Environment: ${env}] [Stage: ${stage}] [Site: ${site}] | [Override: ${override}]"
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            try {
                result = closure.call()
            }
            catch (RuntimeException e) {
                error(e)
            }
        }
        result
    }

}
