/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class DrushUPDB extends Command {

    String site

    def perform() {
        try {
            def output
            def status = executeCommand('drush', [command: 'updatedb-status', site: site])
            if (!status.contains("No database updates required")) {
                output = executeCommand('drush', [command: 'updb', site: site])
            }
            output
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
        }
    }
}
