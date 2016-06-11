/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class CopySiteDB extends CopyCommand {

    String from

    String to

    String site

    String toSite

    String env

    String toEnv

    def perform() {
        def result
        executeInTempDir(subDir: site, caller: this) { tmp ->
            def fromDir = new File(from + '/' + config.docrootPrefixDir + '/' + config.drupalDir)
            def toDir = new File(to + '/' + config.docrootPrefixDir + '/' + config.drupalDir)
            def fileName = "${tmp}/${site}.sql"

            drush command: "sql-dump > ${fileName}", dir: fromDir
            if ((new File(fileName)).exists()) {
                drush command: "sql-drop", dir: toDir, env: toEnv, site: toSite
                result = drush command: "sqlc < ${fileName}", dir: toDir, env: toEnv, site: toSite
                drush command: "rr", dir: toDir, env: toEnv, site: toSite
                drush command: "isa", dir: toDir, env: toEnv, site: toSite
            }
        }
        result
    }

}
