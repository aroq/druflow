/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class GitGetRepo extends Command {

    String repoAddress

    String branch

    String repoDirName

    File dir

    Boolean singleBranch = true

    Integer depth = 1

    def perform() {
        def repoDir = new File(dir, repoDirName)

        debug("Repo dir: ${repoDir}")

        if (config.force) {
            shellCommand cmd: "rm -fR ${repoDirName}", dir: dir
        }

        if (!repoDir.exists()) {
            String options = ''
            if (singleBranch) {
                options += "-b ${branch} --single-branch "
            }
            if (depth) {
                options += "--depth ${depth}"
            }
            shellCommand cmd: "git clone ${options} ${repoAddress} ${repoDirName}", dir: dir
        }

        shellCommand cmd: "git checkout ${branch}",    dir: repoDir
        shellCommand cmd: "git pull origin ${branch}", dir: repoDir
        shellCommand cmd: "git reset --hard",          dir: repoDir
        shellCommand cmd: "git clean -f -d",           dir: repoDir

        repoDir
    }

}
