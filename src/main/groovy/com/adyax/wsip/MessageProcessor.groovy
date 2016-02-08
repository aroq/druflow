/**
 * Created by alex on 29.06.15.
 */

package com.adyax.wsip

class MessageProcessor {

    def process(message) {
        Logger.instance.log("Message processor: ${message}")
        def commands = []

        def commandComponentMatcher = message =~ $/\[(.*)\].*/$
        if (commandComponentMatcher.matches()) {
            commandComponentMatcher[0][1].tokenize(';').each { component ->
                Logger.instance.log("Message processor command: ${component}")
                def commandMatcher = component =~ $/(.*)\((.*)\)/$
                if (commandMatcher.matches()) {
                    def componentName
                    def commandName
                    (componentName, commandName) = commandMatcher[0][1].tokenize(':')
                    def commandParams = commandMatcher[0][2].tokenize(',')
                    commands.push([(componentName): [(commandName):  commandParams*.trim()]])
                }
            }
        }
        commands
    }
}
