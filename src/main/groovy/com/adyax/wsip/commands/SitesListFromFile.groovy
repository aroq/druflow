/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesListFromFile extends Command {

    String fileName

    def perform() {
        def sites = []

        File file = new File(workspace(), fileName)
        if (file.exists()) {
            file.eachLine { line ->
                sites << line
            }
        }
        else {
            log "File ${file} not found."
        }

        return sites
    }
}
