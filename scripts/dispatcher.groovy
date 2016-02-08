/**
 * Created by alex on 22.09.15.
 */
import hudson.model.*
import hudson.model.queue.*
import hudson.model.labels.*
import org.jvnet.jenkins.plugins.nodelabelparameter.*

def failBuild(msg)
{
    throw new RuntimeException("[GROOVY] User message, exiting with error: " + msg)
}

def skipBuild()
{
    throw new RuntimeException("[GROOVY] : Skipping build as there is no config file for the repo")
}

try {
    // Get the current build job
    def thr = Thread.currentThread()
    def build = thr?.executable

    // Get repo group and name
    def repo_dir = build.buildVariables.get("repo_dir")
    manager.listener.logger.println("repo_dir:")
    manager.listener.logger.println(repo_dir)
    def dirs = repo_dir.tokenize('/')
    def reversedDirs = dirs.reverse()
    def repoNameFull = reversedDirs[0]
    def repoNameParts = repoNameFull.tokenize('.')
    def repoName = repoNameParts[0]
    def repoGroup = reversedDirs[1]
    manager.listener.logger.println("repoName: " + repoName)
    manager.listener.logger.println("repoGroup: " + repoGroup)

    // Get config for the current git repo
    def workspace = build.workspace
    manager.listener.logger.println("WORKSPACE:" + workspace)


    try {
        config = new ConfigSlurper().parse(new File("${workspace}/${repoGroup}.${repoName}.groovy").text)
    }
    catch (Exception e) {
        manager.listener.logger.println("No config file exists for repo, skipping build.")
        skipBuild()
    }

    config.docroots.each {
        def docroot = it.value

        // Get the parameters for the current build job
        def currentParameters = build.getAction(ParametersAction.class)?.getParameters() ?:
                failBuild("There are no parameters to pass down.")



        def version = build.buildVariables.get("version")
        def devGroupName = build.buildVariables.get("dev_group_name")

        def gitURL = build.buildVariables.get("git_url")

        manager.listener.logger.println("VERSION: $version")

        def targetJobObject = null
        def groupName = docroot.groupName

        if (version == "develop")
        {
            targetJobName = "deploy-DEV"
            groupName = docroot.groupName
        }
        else if (version == "master")
        {
            targetJobName = "deploy-STAGE"
            groupName = docroot.groupName
        }
        else if (version == "state_stable")
        {
            targetJobName = "build-STABLE"
            groupName = docroot.docrootGroupName
        }
        else if (version == "commands")
        {
            targetJobName = "execute-commands"
            groupName = docroot.docrootGroupName
        }

        def repoAddress = "git@${gitURL}:${repoGroup}/${repoName}.git"

        targetJobObject = Hudson.instance.getItem(groupName).getItem(devGroupName).getItem(targetJobName) ?:
                failBuild("Could not find a build job with the name $targetJobName. (Are you sure the spelling is correct?)")

        manager.listener.logger.println("$targetJobObject, $targetJobName")

        def params = targetJobObject.getProperty(ParametersDefinitionProperty.class)
        manager.listener.logger.println("Params: $params")

        // Add parameters for downstream build
        def newParameters = new ArrayList(currentParameters);
        newParameters.add(new StringParameterValue('project', docroot.project))
        newParameters.add(new StringParameterValue('projectName', docroot.project))
        newParameters.add(new StringParameterValue('group_url', repoGroup))
        newParameters.add(new StringParameterValue('project_url', repoName))
        newParameters.add(new StringParameterValue('force', docroot.force))
        newParameters.add(new StringParameterValue('repoAddress', repoAddress))

        for (param in params.getParameterDefinitions()) {
            manager.listener.logger.println("Default param: ${param.name}")
            try {
                if (param.name in ["config_repo", "workspace", "debug", "simulate"]) {
                    newParameters.add(new StringParameterValue(param.name, param.defaultValue))
                    manager.listener.logger.println(param.name + " = " + param.defaultValue)
                }
            }
            catch(Exception e) {
            }
        }

        def buildNumber = targetJobObject.getNextBuildNumber()

        // Add information about downstream job to log
        def jobUrl = targetJobObject.getAbsoluteUrl()
        manager.listener.logger.println("Starting downstream job $targetJobName ($jobUrl)" +  "\n")
        manager.listener.logger.println("======= DOWNSTREAM PARAMETERS =======")
        manager.listener.logger.println("$newParameters")

        // Start the downstream build job if this build job was successful
        boolean targetBuildQueued = targetJobObject.scheduleBuild(5,
                new Cause.UpstreamCause(build),
                new ParametersAction(newParameters)
        );

        if (targetBuildQueued)
        {
            manager.listener.logger.println("Build started successfully")
            manager.listener.logger.println("Console (wait a few seconds before clicking): $jobUrl$buildNumber/console")
        }
        else
            failBuild("Could not start target build job")
    }


}
catch (Exception e) {
    manager.listener.logger.println("Exception: " + e)
}