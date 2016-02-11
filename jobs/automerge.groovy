import com.github.aroq.druflow.commands.AutoMergeJobDSLCreator
import com.github.aroq.groovycommon.Common
import com.github.aroq.groovycommon.Config

config = Config.instance.config

Config.instance.addParams(System.properties)

// Determine current environment.
try {
    WORKSPACE
    config.environment = 'jenkins'
} catch (MissingPropertyException mpe) {
    config.environment = 'local'
}

//if (config.environment == 'local') {
//    config.gitlabAddress = System.getProperty('gitlabAddress')
//    config.projectID = System.getProperty('projectID')
//    config.privateToken = System.getProperty('privateToken')
//}
//else {
//    config.gitlabAddress = gitlabAddress
//    config.projectID = projectID
//    config.privateToken = privateToken
//}

config.rootDir = Common.instance.rootDir()

// Params from config file.
def configFile = new File(config.rootDir + '/config/config.groovy')
Config.instance.addParams(ConfigSlurper.newInstance(config.environment).parse(configFile.text))

(new AutoMergeJobDSLCreator(componentName: 'automerge', scriptObject: this)).execute()

