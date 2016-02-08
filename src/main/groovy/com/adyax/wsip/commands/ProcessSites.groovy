/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ProcessSites extends Command {

    def urlPatterns

    def urlEnvNames

    def perform() {
        def sites = executeCommand('siteListAll')

        sites.each { site->
            // Add db.
            def dbInfo = executeCommand('dbInfo', [site: site])
            if (!dbInfo) {
                executeCommand('dbAdd', [site: site])
            }

            // Add urls.
            def reversedSiteName = site.replaceAll(/[._-]/, '.').tokenize('.').reverse().join('-')
            urlEnvNames.each { envName ->
                urlPatterns[envName]?.each { pattern ->
                    def url = pattern.replace('{reversed-site-name}', reversedSiteName)
                    def domainInfo = executeCommand('domainInfo', [argument: url, env: envName, site: site])
                    if (!domainInfo) {
                        executeCommand('domainAdd', [argument: url, env: envName, site: site])
                    }
                }
            }
        }

    }

}
