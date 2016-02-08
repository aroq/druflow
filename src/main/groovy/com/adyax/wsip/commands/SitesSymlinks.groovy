/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class SitesSymlinks extends Command {

    String dir

    def perform() {
        executeCommand('siteListAll', [sitesDir: dir]).each { site ->
            siteSymlinks site: site, dir: new File(dir)
        }
    }

}
