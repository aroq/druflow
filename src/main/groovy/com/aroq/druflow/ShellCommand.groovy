package com.aroq.druflow

class ShellCommand {

    String cmd

    File dir

    Boolean returnExitValue = false

    def timeout = 60000

    def perform() {
        try {
            def exitValue

            def proc = ['bash', '-c', '-l', cmd].execute(null, dir)
            def output = new StringBuilder()
            def err = new StringBuilder()
            proc.consumeProcessOutput(output, err)
            proc.waitForOrKill(timeout)

            exitValue = proc.exitValue()

            if (returnExitValue) {
                return exitValue
            }

            if (exitValue != 0) {
                throw new RuntimeException("Command finished with error")
            }

            output.toString()
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            throw e
        }
    }
}
