/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class Drush extends Command {

    String site

    String projectName

    File dir = drupalDir()

    Boolean withoutAlias = false

    String env

    String mode

    def validateParams() {
        mode = mode ? mode : 'default'
        if (command == 'drush') {
            command = config.command
        }
    }

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

            if (mode == 'default') {
                if (site && !withoutAlias) {
                    // TODO: env.site separator config.
                    command = "${drush} --yes @${env}.${site} ${command}"
                } else {
                    command = "${drush} --yes ${command}"
                }
                output = executeCommand('shellCommand', [cmd: command, dir: dir])
            }
            else if (mode == 'backend') {
                def rootPath = (new JsonSlurper().parseText(extractJson(executeCommand('drush', [noSimulate: true, site: site, mode: 'default', command: 'status --format=json']))))['%paths']['%root']

                def backends = (new JsonSlurper().parseText(executeCommand('getAcquiaServers', [site: site, mode: 'default']))).findAll { it ->
                    it.services.containsKey('web')
                }.collect { it -> it.fqdn }
                backends.each { backend ->
                    def cmd = "${drush} \"${config.acquiaCloudDocrootName}.${env}@${backend}${rootPath}#${site}\" --yes ${command} --ssh-options=\"-o StrictHostKeyChecking=no\""
                    output += executeCommand('shellCommand', [cmd: cmd, dir: dir])
                }
            }

            output
        }
        catch (InterruptedException e) {
            throw e
        }
        catch (Exception e) {
            error(e)
            throw e
        }
    }
}
