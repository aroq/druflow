/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ClearVarnish extends Command {

    String site

    def perform() {
        def domains = executeCommand('drush', [site: site, command: 'ap-domains', noSimulate: true])
        if (domains) {
            domains.eachLine {domain ->
                executeCommand('clearVarnishForURL', [argument: domain, site: site])
            }
        }
    }
}
