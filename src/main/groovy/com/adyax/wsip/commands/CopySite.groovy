/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class CopySite extends Command {

    String from

    String to

    String site

    String toSite

    String env

    String toEnv

    def perform() {
        def result = []
        result << executeCommand('copySiteDB', [from: from, to: to, site: site, toSite: toSite, env: env, toEnv: toEnv])
        result << executeCommand('copySiteFiles', [from: from, to: to, site: site, toSite: toSite, env: env, toEnv: toEnv])
        result
    }

}
