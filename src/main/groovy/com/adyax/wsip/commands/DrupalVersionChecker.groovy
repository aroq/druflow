/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class DrupalVersionChecker extends Command {

    def status

    def perform() {
        def release = getReleaseByStatus(status)
        def installedVersion = getInstalledDrupalVersion()
        def versionsMatched = release.version == installedVersion
        log versionsMatched ? "${status} version and installed versions are matched." : "${status} version and installed versions are not matched."
        versionsMatched ? [result: 0] : [result: 1, installedVersion: installedVersion, lastVersion: release.version, releaseDate: release.date, releaseStatus: release.status] // If 1 then need update
    }

    def getReleases() {
        def releases = executeCommand('drush', [command: 'rl --format=json', dir: tmpDir(), noSimulate: noSimulate])
        def parsedReleases = JsonSlurper.newInstance().parseText(releases)
        assert parsedReleases
        parsedReleases
    }

    def getReleaseByStatus(status) {
        def result = null
        getReleases()?.each { release ->
            def statuses = getReleaseStatuses(release)
            if (statuses.contains(status)) {
                result = release.value
            }
        }
        result
    }

    def getReleaseStatuses(release) {
        release.value.status.tokenize(', ')
    }

    def getInstalledDrupalVersion() {
        def tmp = tmpDir()
        tmp.mkdirs()
        def result

        def dir = executeCommand('gitGetRepo', [repoDirName: 'drupal', dir: tmp])
        new File(dir, 'includes/bootstrap.inc').eachLine { line ->
            def matcher = line =~ /define\('VERSION',\s.*'(.*)'\);/
            if (matcher.matches()) {
                result = matcher[0][1]
            }
        }
        result
    }

}
