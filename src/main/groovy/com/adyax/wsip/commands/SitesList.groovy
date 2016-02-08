/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesList extends Command {

    def builders

    def perform() {
        def sites = []
        if (!builders) {
            debug "No site list builders provided."
        }
        builders.each { builder ->
            debug "Site list builder: ${builder}"
            def result = executeCommand(builder)
            if (sites.size() == 0) {
                sites = result
            }
            else {
                // TODO: Choose right operation based on config.
                sites = sites.intersect(result)
            }
        }
        return sites
    }
}
