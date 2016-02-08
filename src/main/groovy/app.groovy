/**
 * Created by Aroq on 07/02/16.
 */

import com.adyax.wsip.*
import com.adyax.wsip.utils.Common
import groovy.json.*

println "Groovy version: ${GroovySystem.version}"

config = Config.instance.config

Config.instance.addParams(System.properties)

// Determine current environment.
def environment
try {
    build
    environment = 'jenkins'
    config.build = build
} catch (MissingPropertyException mpe) {
    environment = 'local'
}

config.environment = environment
config.binding     = binding

if (config.environment == 'local') {
//    def cli = new CliBuilder(usage: 'groovy app.groovy -[dh] COMMAND')
//    cli.with {
//        d longOpt: 'debug', 'Debug mode'
//        s longOpt: 'simulate', 'Simulate mode'
//        c longOpt: 'config-path', args: 1, argName: 'config-path', 'Path to config file'
//        p longOpt: 'properties-path', args: 1, argName: 'properties-path', 'Path to properties file'
//        h longOpt: 'help', 'Help'
//    }
//
//    cli.D(args: 1, argName:'property=value', 'use value for given property')
//
//    def options = cli.parse(binding.variables.args)
//
//    if (!options) {
//        return
//    }
//
//    if (options.d) {
//        config.debug = true
//    }
//
//    if (options.s) {
//        config.simulate = true
//    }
//
//    if (options.D) {
//        options.Ds.each { paramWithValue ->
//            parts = paramWithValue.tokenize('=')
//            config[parts[0]] = parts[1]
//        }
//    }
//
//    if (options.h) {
//        cli.usage()
//        return
//    }
//
//    if (options.c) {
//        file = new File(options.c)
//        if (file.exists()) {
//            config.configPath = options.c
//        }
//        else {
//            println "Config file does not exist: ${options.c}"
//            return
//        }
//    }
//
//    if (options.p) {
//        Properties properties = new Properties()
//        File propertiesFile = new File(options.p)
//        if (propertiesFile.exists()) {
//            propertiesFile.withInputStream {
//                properties.load(it)
//            }
//            properties.each { property ->
//                config."${property.key}" = property.value
//                println "${property.key} = ${property.value}"
//            }
//        }
//        else {
//            println "Properties file does not exist: ${options.c}"
//            return
//        }
//    }
//
//    def extraArguments = options.arguments()
//
//    if (extraArguments.size() == 0) {
//        cli.usage()
//        return
//    }
//
//    config.executeCommand = extraArguments[0]

    config.executeCommand = System.getProperty("executeCommand")
    config.workspace = System.getProperty("workspace")
}

config.rootDir = Common.instance.rootDir()
// Params from config file.
def configFile = new File(config.rootDir + '/config/config.groovy')
Config.instance.addParams(ConfigSlurper.newInstance(environment).parse(configFile.text))

// Add binding parameters.
Config.instance.addParams(binding.getVariables())

// Register logger with out variable.
Logger.instance.registerOut(config.out)

try {
    def output = Common.instance._executeCommand('app', this)
    println JsonOutput.toJson(output)
}
catch (InterruptedException e) {
    Logger.instance.error("Interrupted")
}
