package com.github.aroq.druflow.commands

import com.github.aroq.groovycommon.commands.Command

/**
 * Created by Aroq on 10/02/16.
 */
abstract class JobDSLCreator extends Command {

//    protected def config

    def componentName

    protected def componentConfig

    def scriptObject

//    def log(message) {
//        if (config.environment == 'jenkins') {
//            scriptObject.out.println message
//        }
//        else {
//            println message
//        }
//    }

    def loadConfig() {
        if (config.environment == 'jenkins') {
            return scriptObject.readFileFromWorkspace("config/${componentName}.groovy")
        }
        else {
            def configFile = new File(rootDir(), "config/${componentName}.groovy")
            if (configFile.exists()) {
               return configFile.text
            }
        }
    }

//    def rootDir() {
//        def dir = new File(System.getProperty("user.dir"))
//        if (dir.exists()) {
//            return dir.getPath()
//        }
//    }

//    abstract def process()

    abstract def createJob(params)
}
