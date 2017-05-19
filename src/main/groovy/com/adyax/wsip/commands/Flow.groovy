/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands


import com.adyax.wsip.dsl.FlowDSL
import com.adyax.wsip.dsl.FlowScript
import org.codehaus.groovy.control.CompilerConfiguration

class Flow extends Command {

    String flowName

    String flowType

    String env

    String projectName

    Boolean noBackup

    Boolean noAllSites

    Boolean noDeployTag

//    def transformParams() {
//        super.transformParams()
//        if (!flowType) {
//            flowType = 'deploy'
//        }
//    }

    def perform() {
        if (noAllSites) {
            log "Single site deploy."
        }
        if (noBackup) {
            log "No backup will be performed."
        }
        if (noDeployTag) {
            log "No tag deploy will be performed."
        }

        def output = ''
        FlowDSL flowDsl = new FlowDSL(context: config.contexts.core, flowName: flowName, env: env);
        Binding binding = new Binding([
                flow: flowDsl,
                projectName: projectName,
                noAllSites: noAllSites,
                noBackup: noBackup,
                noDeployTag: noDeployTag,
                flowType: flowType,
                env: env
        ]);

        def config = new CompilerConfiguration(scriptBaseClass: FlowScript.class.name)

        GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config);

        def file = scriptsFile("${flowName}.flow.steps")
        if (file) {
            try {
                output = shell.evaluate(file)
            }
            catch (InterruptedException e) {
                throw e
            }
            catch (Exception e) {
                error(e)
            }
        }
        output
    }

    def scriptsFile(fileName) {
        def file = new File(docrootConfigDir() + '/' + fileName)
        if (file.exists()) {
            return file
        }
        else {
            file = scriptsDirFile(fileName)
            return file
        }

    }
}
