/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesListProjectName extends Command {

    def projectName

    def perform() {
        def sites = []

        // Match all project.*
        def realName = escapedName(transformProjectName(projectName + '.'))
        debug "Match all for: ${realName}.*"
        new File(sitesDir()).eachDirMatch(~/${realName}.*/) { dir ->
            sites.push(dir.getName())
            debug("Found site: ${dir.getName()}")
        }

        // Match sites for all project name parts.
        def pattern = ''
        projectName.tokenize('.').each { part ->
            pattern = pattern != '' ? pattern + '.' + part : part
            def patternRealName = escapedName(transformProjectName(pattern))
            debug "Match all for: ${patternRealName}"
            new File(sitesDir()).eachDirMatch(~/${patternRealName}/) { dir ->
                if (!sites.contains(dir.getName())) {
                    sites.push(dir.getName())
                    debug("Found site: ${dir.getName()}")
                }
            }
        }

        return sites
    }
}
