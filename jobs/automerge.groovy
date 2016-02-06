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
    config.repoPath = System.getProperty('repoPath')
}
else {
    config.repoPath = WORKSPACE
}

(new Component(componentName: 'automerge', scriptObject: this, config: config)).processComponent()

