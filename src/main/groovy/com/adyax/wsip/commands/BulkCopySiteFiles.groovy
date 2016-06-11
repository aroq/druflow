/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class BulkCopySiteFiles extends Command {

    String fileName

    String to

    def perform() {
        def sites
        if (fileName) {
            sites = executeCommand('sitesListFromFile')
        }
        else {
            sites = executeCommand('siteListAll', [sitesDir: to + '/' + config.docrootPrefixDir + '/' + config.sitesDir])
        }
        sites.each { site ->
            log site
            executeCommand('copySiteFiles', [site: site])
        }
    }

}
