/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class GitRepoChanged extends Command {

    File repoDir

    def perform() {
        def tmp = docrootTmpDir()

        if (!repoDir.exists()) {
            throw new RuntimeException("Repo dir does not exists.")
        }

        shellCommand cmd: "git rev-parse --verify HEAD >/dev/null || echo 1", dir: repoDir
        shellCommand cmd: "git update-index -q --ignore-submodules --refresh", dir: repoDir
        Integer d1 = shellCommand cmd: "git diff-files --quiet --ignore-submodules", returnExitValue: true, dir: repoDir
        Integer d2 = shellCommand cmd: "git diff-index --cached --quiet --ignore-submodules HEAD --", returnExitValue: true, dir: repoDir
        Integer d3 = shellCommand cmd: "test -z \"\$(git status --porcelain)\"", returnExitValue: true, dir: repoDir

        return d1 + d2 + d3
    }

}
