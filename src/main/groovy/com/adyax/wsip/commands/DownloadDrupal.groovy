/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class DownloadDrupal extends Command {

    String sourceDirName = 'drupal'

    File dir

    String lastVersion

    def perform() {
        def drupal = lastVersion ? "drupal-${lastVersion}" : 'drupal'
        drush command: "dl ${drupal} --drupal-project-rename=${sourceDirName}", dir: dir, noSimulate: noSimulate
    }

}
