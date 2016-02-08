/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class DeployTagCapistrano extends Command {

    String tag

    String capRepo

    def perform() {
        def capDir = executeCommand('gitGetRepo', [repoAddress: capRepo, branch: 'master', dir: tmpDir(), repoDirName: 'capistrano'])
        executeCommand('shellCommand', [cmd: "cap prod deploy -S reference=${tag}", dir: capDir])
    }

}
