/**
 * Created by alex on 02.07.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class AcquiaCloudCommand extends Command {

    String argument

    String acquiaCloudCommandName

    Boolean wait

    String site

    String env

    String defaultACEnv

    def transformParams() {
        super.transformParams()

        // TODO: Refactor it.
        if (!argument) {
            switch (acquiaCloudCommandName) {
                case 'ac-code-path-deploy':
                    if (config.tag) {
                        debug "'argument' param is not set, using tag param instead: ${config.tag}."
                        argument = "tags/${config.tag}"
                    }
                    break
                case 'ac-database-info':
                case 'ac-database-add':
                case 'ac-database-instance-backup':
                    argument = site.replaceAll("\\.|-", '_')
                    break
            }
        }

        if (!env) {
            env = defaultACEnv
        }
    }

    def perform() {
        def task = executeCommand('drush', [command: getCommand(), site: site, env: env, noSimulate: noSimulate])
        def count = 0
        if (wait && !simulate) {
            task = JsonSlurper.newInstance().parseText(task)
            def waitTask = [:]
            while (!waitTask.state || (waitTask.state != 'done' && waitTask.state != 'error')) {
                count++
                log("Wait until command is finished: attempt ${count}")
                Thread.sleep(3000)
                waitTask = JsonSlurper.newInstance().parseText(executeCommand('waitCommandFinish', [argument: task.id, site: site, env: env, noSimulate: noSimulate]))
                if (count == 50) {
                    error("Too many attempts")
                    break
                }
            }
            debug("Task status: ${waitTask.state}")
            debug("Task description: ${waitTask.description}")
            if (waitTask.state == 'done') {
                log("Command wait finished successfully.")
            }

        }

        return task
    }

    def getCommand() {
        "${acquiaCloudCommandName} ${argument} --format=json --ac-site=${config.acquiaCloudDocrootName} --ac-realm=prod --ac-env=${env} --strict=0"
    }

    def getMasterSite(site) {
        def instance = site.replaceAll(/[._-]/, '.').tokenize('.').last()
        (site - instance) + 'master'
    }

}
