/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ShellCommand extends Command {

    String cmd

    File dir

    Boolean returnExitValue = false

    def timeout = 600000

    def transformParams() {
        super.transformParams()
        if (config.timeout) {
            timeout = config.timeout.toInteger()
        }
    }

    def perform() {
        commandTitle = cmd
        try {
            log("${cmd}")

            def exitValue

            def proc = ['bash', '-c', '-l', cmd].execute(null, dir)
            def out = new StringBuilder()
            def err = new StringBuilder()
            proc.consumeProcessOutput(out, err)
            proc.waitForOrKill(timeout)

            exitValue = proc.exitValue()

            if (returnExitValue) {
                return exitValue
            }

            log "${out.toString()}---"
            log "${err.toString()}---"

            if (exitValue != 0) {
                error("Command exit code: ${exitValue}")
                log "${err.toString()}---"
                throw new RuntimeException("Command finished with error")
            }

            out.toString()
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
            throw e
        }
    }
}
