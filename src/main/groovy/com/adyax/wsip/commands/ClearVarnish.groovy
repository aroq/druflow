/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class ClearVarnish extends Command {

    String site

    String mode

    def perform() {
        def domains
        if (mode == 'site') {
            domains = executeCommand('drush', [site: site, command: 'ap-domains', noSimulate: true])
        }
        // TODO: extract needed sites from drubone config.
        if (mode == 'all') {
            def commandResult = executeCommand('domainList', [env: config.env, site: site])
            log (commandResult)
            def domainList = JsonSlurper.newInstance().parseText(commandResult)
            domains = domainList.collect {
                it.name
            }.flatten()
        }
        if (domains) {
            domains.eachLine {domain ->
                executeCommand('clearVarnishForURL', [argument: domain, site: site])
            }
        }
    }
}
