/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesListAll extends Command {

    def sitesDir = sitesDir()

    def perform() {
        def sites = [:]
        Integer order

        new File(sitesDir).eachDirMatch(~/.*/) { dir ->
            def settingsFile = new File(dir.toString() + '/settings.php')
            if (settingsFile.exists()) {
                order = 100
                if (config.siteListOrder && config.siteListOrder.containsKey(dir.getName())) {
                    order = config.siteListOrder[dir.getName()]
                }
                sites.put(dir.getName(), order)
            }
        }
        sites = sites.sort { it.value }
        sites = sites.keySet() as String[]

        return sites
    }
}
