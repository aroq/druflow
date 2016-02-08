/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesSymlinksInGit extends Command {

    String branch

    def perform() {
        executeInTempDirAndPushToGit(repoDirName: 'sites', branch: branch, message: '[skip] Symlinks auto-updated', caller: this) { dir ->
            executeCommand('sitesSymlinks', [dir: dir.toString()])
        }
    }

}
