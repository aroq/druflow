import com.aroq.druflow.Component

def config = [:]

// Determine current environment.
try {
    WORKSPACE
    config.environment = 'jenkins'
} catch (MissingPropertyException mpe) {
    config.environment = 'local'
}

if (config.environment == 'local') {
    config.gitlabAddress = System.getProperty('gitlabAddress')
    config.projectID = System.getProperty('projectID')
    config.privateToken = System.getProperty('privateToken')
}
else {
    config.gitlabAddress = gitlabAddress
    config.projectID = projectID
    config.privateToken = privateToken
}

(new Component(componentName: 'automerge', scriptObject: this, config: config)).processComponent()

