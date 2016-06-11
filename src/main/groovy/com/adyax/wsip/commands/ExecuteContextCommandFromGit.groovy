/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.ContextScript

class ExecuteContextCommandFromGit extends Command {

    String site

    String project

    def perform() {
        config.stopLogging = true
        def tmp = docrootTmpDir()
        tmp.mkdirs()
        File script
        def dir = executeCommand('gitGetRepo', [repoDirName: project, dir: tmp, branch: 'commands'])
        script = new File(dir, 'commands')
        try {
            if (script.exists()) {
                config.stopLogging = false
                executeContextCommand script: script
            }
        }
        finally {
            config.stopLogging = true
            script.delete()
            new File(dir, 'commands').createNewFile()
            executeCommand('gitPushRepo', [repoDir: dir, branch: 'commands', message: '[skip] Removed commands after execution'])
            config.stopLogging = false
        }
    }

}
