/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class GitPushRepo extends Command {

    String branch

    File repoDir

    String message

    def perform() {
        shellCommand cmd: "git checkout ${branch}", dir: repoDir
        shellCommand cmd: "git pull origin ${branch}", dir: repoDir
        def output = shellCommand cmd: "git status", dir: repoDir
        def repoChanged = gitRepoChanged repoDir: repoDir
        if (repoChanged) {
            shellCommand cmd: "git add -A", dir: repoDir
            shellCommand cmd: "git commit -m \"${message}\"", dir: repoDir
            shellCommand cmd: "git push origin ${branch}", dir: repoDir
        }

        output
    }

}
