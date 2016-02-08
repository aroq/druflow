/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ExecuteCommandInGitDir extends Command {

    String project

    String branch = 'master'

    String cmd

    def perform() {
        def result
        executeInTempGitDir(repoDirName: project, singleBranch: false, caller: this, depth: 0) { tmp ->
            result = executeCommand('shellCommand', [cmd: cmd, dir: tmp])
        }
    }

}
