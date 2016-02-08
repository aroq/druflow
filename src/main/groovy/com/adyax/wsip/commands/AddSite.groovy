/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class AddSite extends Command {

    String site

    String dir

    String siteDirTemplate = 'default/sitedir.template'

    def perform() {
        // Copy files.
        def sites = executeCommand('siteListAll', [sitesDir: dir])
        if (!(site in sites)) {
            log "Copying ${siteDirTemplate} to ${site}"
            shellCommand cmd: "cp -fR ${siteDirTemplate} ${site}", dir: new File(dir)
        }
    }

}
