package com.adyax.wsip.dsl

import com.adyax.wsip.Base

/**
 * Created by alex on 02.08.15.
 */
class BaseDSL extends Base {

    def result = []

    String stage

    String env

    def executeClosure(closure) {
        if (closure) {
            closure.delegate = this
            closure.resolveStrategy = Closure.DELEGATE_FIRST
            result += closure.call()
        }
    }

}
