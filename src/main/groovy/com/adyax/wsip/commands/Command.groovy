/**
 * Created by alex on 02.07.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.*
import com.adyax.wsip.utils.PropertiesHandler

class Command extends Base {

    def command

    def className

    def requiredParams

    def requiredConfigParams

    Boolean logTime

    Integer startTime

    Integer endTime

    String commandTitle

    Boolean noSimulate

    Boolean simulate

    Boolean writeResultToProperties = false

    String propFileName = 'result.properties'

    def loadParams() {
    }

    def validateParams() {
        requiredParams.each { requiredParam ->
            assert this."${requiredParam}"
        }

        requiredConfigParams.each { requiredParam ->
            assert config.containsKey(requiredParam)
        }
    }

    def dumpParams() {
        getCommandParams().each { field ->
            if (field instanceof Closure || field.type.name == 'groovy.lang.Closure') {
                debug("param: ${field.name} = Closure")
            }
            else {
                def value = thisObject."${field.name}"
                debug("Command param: ${field.name} = ${value}")
            }
        }
    }

    def beforeExecute() {
        debug("Command START - ${command}", this)
        startTime = System.currentTimeMillis()
        executeCount++
        setCaller(this)
    }

    def getCommandParams() {
        this.class.getDeclaredFields().findAll {!it.synthetic}
    }

    def transformParams() {
        getCommandParams().each {field ->
            if (!thisObject."${field.name}") {
                if (config.containsKey(field.name)) {
                    if (field.type.name == 'java.lang.Boolean') {
                        if (config[field.name] == 'false' || config[field.name] == '0') {
                            thisObject."${field.name}" = false
                        }
                        else {
                            thisObject."${field.name}" = config[field.name].asBoolean()
                        }
                    }
                    else {
                        thisObject."${field.name}" = config[field.name]
                    }
                }
            }
        }

        if (noSimulate) {
            simulate = false
        }
        else {
            simulate = config.simulate
        }
    }

    def execute() {
        try {
            def propFile = new File(workspace(), propFileName)
            propFile.delete()
            loadParams()
            transformParams()
            validateParams()
            beforeExecute()
            dumpParams()
            def result = perform()

            if (writeResultToProperties) {
                PropertiesHandler propertiesHandler = new PropertiesHandler(propFile: propFile)
                propertiesHandler.writeProperties(result)
            }

            return result
        }
        finally {
            afterExecute()
        }
    }

    def afterExecute() {
        endTime = System.currentTimeMillis()
        if (logTime) {
            log "Command execution time: ${(endTime - startTime) / 1000.0} seconds, command - ${commandTitle ? commandTitle : command}"
        }
        debug("Command END - ${command}", this)
        executeCount--
    }

    def simulate() {
        config.simulate && config.simulate != '0'
    }

    def debug() {
        config.debug && config.debug != '0'
    }

    def fail(message) {
        throw new RuntimeException("Exiting with error: ${message}")
    }
}
