package com.adyax.wsip

/**
 * Created by alex on 09.08.15.
 */
class Context {

    String name

    ArrayList allowedCommands

    LinkedHashMap commands

    Boolean checkSites = false

    def commandAllowed(commandName, params) {
        if (commands && commands[commandName]) {
            if (commands[commandName]?.allowedEnvironments) {
                if (params['env'] && !(params['env'] in commands[commandName].allowedEnvironments)) {
                    throw new RuntimeException(/Environment "${params['env']}" is not allowed for "${commandName}" command in "${name}" context/)
                }
            }
            if (commands[commandName]?.allowedOptions) {
                if (params['command']) {
                    def options = params['command'].findAll(/--(.*)=/) { match, option -> option}
                    options += params['command'].findAll(/-([^-])\s/) { match, option -> option}
                    options.each {option ->
                        if (!(option in commands[commandName].allowedOptions)) {
                            throw new RuntimeException(/Option "${option}" is not allowed for "${commandName}" command in "${name}" context/)
                        }
                    }
                }
            }
        }

        if (allowedCommands && !(commandName in allowedCommands)) {
            throw new RuntimeException(/Command "${commandName}" is not allowed in "${name}" context/)
        }

        return true
    }

}
