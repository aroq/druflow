/**
 * Created by alex on 02.07.15.
 */

package com.adyax.wsip.commands

class DBBackupAll extends Command {

    String siteListBuilderCommand

    String dbBackupSiteCommand

    def perform() {
        def result = []
        executeCommand(siteListBuilderCommand).each { site ->
            debug("Backing up database for site: ${site}")
            result.push(executeCommand(dbBackupSiteCommand, [site: site]))
            Thread.sleep(3000)
        }

        return result
    }

}
