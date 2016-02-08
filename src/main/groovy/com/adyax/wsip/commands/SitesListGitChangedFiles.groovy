/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesListGitChangedFiles extends Command {

    String projectName

    String projectLastCommit

    String projectToCommit

    String sitesPattern

    def skipList

    def transformParams() {
        super.transformParams()
        if (!projectToCommit) {
            projectToCommit = 'HEAD'
        }
    }

    def perform() {
        def sites = []

        try {
            if (!projectLastCommit) {
                log("projectLastCommit value is not set.")
                throw new Exception()
            }
            def command = "git diff --name-only ${projectLastCommit} ${projectToCommit}"
            def projectGitDir = new File(projectGitDir(projectName))
            debug("Command: ${command}")
            debug("Project Git directory: ${projectGitDir}")
            def proc = command.execute(null, projectGitDir)

            def exitValue
            def errorText
            def output

            proc.waitFor()

            exitValue = proc.exitValue()
            errorText = proc.err.text
            output = proc.text

            if (exitValue != 0) {
                debug("Command exit code: ${exitValue}")
                debug("Std Err: ${errorText}")
                throw new Exception()
            }

            debug "Command output:\n${output}"

            output.eachLine { changedFile ->
                debug("Changed file: ${changedFile}")
                def matcher = changedFile =~ sitesPattern
                if (matcher.matches()) {
                    if (!sites.contains(matcher[0][1])) {
                        // TODO: replace execute command by direct command name call.
                        sites += executeCommand('siteListByProject', [projectName: matcher[0][1]])
                    }
                } else {
                    if (!skipList.contains(changedFile)) {
                        debug("Not in pattern: ${changedFile}")
                        throw new RuntimeException()
                    }
                }
            }
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            debug("No sites matches.")
            debug(e)
            // TODO: replace execute command by direct command name call.
            sites = executeCommand('siteListByProject')
        }

        return sites
    }
}
