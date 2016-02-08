/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class SiteSymlinks extends Command {

    String site

    String dir

    Boolean force = false

    def perform() {
        // TODO: Refactor this function.
        def dirs = projectDirs site: site, withoutProfiles: true
        def symlinksFile = sitesDirFile('site_symlinks.json')

        if (symlinksFile.exists()) {
            def patterns = JsonSlurper.newInstance().parseText(symlinksFile.text)
            patterns.each { pattern ->
                if (pattern.type == 'directory') {
                    if (force) {
                        shellCommand cmd: "rm -fR ${pattern.source}", dir: siteDir(site, dir)
                    }
                    shellCommand cmd: "mkdir -p ${pattern.source}", dir: siteDir(site, dir)
                }
                dirs.each { targetDir ->
                    def sourceFile = new File(targetDir + '/' + pattern.source)
                    if (sourceFile.exists()) {
                        def source
                        def workingDir
                        def target = tokenReplace(pattern.target, (new File(targetDir)).name)
                        if (pattern.type == 'link') {
                            source = "../.." + (sourceFile.toString() - docrootPrefixDir())
                            workingDir = siteDir(site, dir)
                        }
                        if (pattern.type == 'directory') {
                            source = "../../.." + (sourceFile.toString() - docrootPrefixDir())
                            target = generateLinkName(sourceFile, pattern)
                            workingDir = new File(siteDir(site, dir), pattern.source)
                        }
                        shellCommand cmd: "rm -f ${target}", dir: workingDir
                        shellCommand cmd: "ln -fs ${source} ${target}", dir: workingDir
                    }
                }
            }
        }
    }

    // TODO: refactor it.
    String tokenReplace(String s, projectName) {
        s.replaceAll(/\{projectName\}/, projectName)
    }

    String generateLinkName(File sourceFile, pattern) {
        def dirName = sourceFile.toString() - (projectsDir() + '/') - ('/' + pattern.source)
        dirName.replaceAll('/sites/', '--')
    }

}
