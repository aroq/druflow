/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.utils.PropertiesHandler

class DirUpdate extends Command {

    String sourceDirName = 'drupal'

    String targetDirName = 'repo'

    ArrayList preservedFiles

    def prepareSourceCommand

    def prepareTargetCommand

    String lastVersion

    def perform() {
        def result
        def tmp = tmpDir()
        tmp.mkdirs()

        def sourceDir = prepareSource(tmp)
        def targetDir = prepareTarget(tmp)

        // Remove all files and dirs from target that will be copied over.
        sourceDir.eachFile { file ->
            def targetFile = new File(targetDir, file.name)
            if (targetFile.isDirectory()) {
                targetFile.deleteDir()
            }
            else {
                targetFile.delete()
            }
        }

        // Copy files.
        shellCommand cmd: "cp -fR ${sourceDirName}/* ${targetDirName}", dir: tmp

        def repoChanged = gitRepoChanged repoDir: new File(tmp, targetDirName)

        log repoChanged ? "Target is updated" : "Target is not updated"

        if (!simulate()) {
            executeCommand('gitPushRepo', [repoDir: targetDir, message: "Drupal updated to ${lastVersion}"])
        }

        repoChanged ? 1 : 0
    }

    def prepareSource(dir) {
        new File(dir, sourceDirName).deleteDir()
        executeCommand(prepareSourceCommand, [sourceDirName: sourceDirName, dir: dir])

        // Remove unneeded files.
        preservedFiles?.each { preservedFile ->
            shellCommand cmd: "rm -fR ${sourceDirName}/${preservedFile}", dir: dir
        }

        new File(dir, sourceDirName)
    }

    def prepareTarget(dir) {
        shellCommand cmd: "rm -fR ${targetDirName}", dir: dir
        executeCommand(prepareTargetCommand, [repoDirName: targetDirName, dir: dir])
        new File(dir, targetDirName)
    }

}
