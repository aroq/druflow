/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class CopySiteFiles extends Command {

    String from

    String to

    String env

    String site

    def perform() {
        executeInTempDir(subDir: site, caller: this) { tmp ->
            def fromDir = new File(from + '/' + config.docrootPrefixDir + '/' + config.drupalDir)
            def toDir = new File(to + '/' + config.docrootPrefixDir + '/' + config.drupalDir)

            def result

            // TODO: env.site separator config.
            drush command: "rsync @${env}.${site}:%files ${tmp}", dir: fromDir, withoutAlias: true
            result = drush command: "rsync ${tmp}/files/. @${env}.${site}:%files", dir: toDir, withoutAlias: true
            result
        }
    }

}
