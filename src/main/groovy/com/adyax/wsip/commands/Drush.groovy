/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class Drush extends Command {

    String site

    String projectName

    File dir = drupalDir()

    Boolean withoutAlias = false

    String env

    def perform() {
        def output
        if (!env) {
            env = config.env
        }
        try {
            // TODO: refactor it.
            if (context && context.checkSites && projectName) {
                def sites = executeCommand('siteListByProject', [projectName: projectName])
                assert site in sites
            }
            def drush = config.drush ? config.drush : 'drush'
            if (simulate) {
                command = "-s ${command}"
            }
            if (site && !withoutAlias) {
                // TODO: env.site separator config.
                command = "${drush} --yes @${env}.${site} ${command}"
            }
            else {
                command = "${drush} --yes ${command}"
            }

            output = executeCommand('shellCommand', [cmd: command, dir: dir])
            output
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
            return output
        }
    }
}
