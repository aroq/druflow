contextParams {
    defaultContext {
        name = 'defaultContext'
        allowedCommands = []
    }
    core {
        name = 'core'
        allowedCommands = []
    }
    docroot {
        name = 'docroot'
        allowedCommands = []
    }
    project {
        name = 'project'
        allowedCommands = ['drush']
        commands {
            drush {
//                allowedOptions = ['d', 'y', 'n', 'v', 's', 'debug', 'verbose', 'yes', 'no', 'simulate']
//                allowedEnvironments = ['dev', 'test']
            }
        }
    }
}

// Default values.
docrootDir = ''
//env = 'local'
defaultACEnv = 'dev'
debug = false
docrootPrefixDir = ''
drupalDir = 'docroot'
sitesDir = 'sites'
projectsDir = 'code'
sitesPattern = $/sites/([^/]*)/.*/$

commands {
    app {
        className = 'com.adyax.wsip.Application'
        requiredConfigParams = ['workspace', 'docrootDir']
    }
    dbBackupAll {
        className = 'com.adyax.wsip.commands.DBBackupAll'
        siteListBuilderCommand = 'siteListAll'
        dbBackupSiteCommand = 'dbBackupSite'
    }
    dbBackupSite {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-database-instance-backup'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
        wait = true
    }
    dbCopyAC {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-database-copy'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
        wait = true
    }
    deployTag {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-code-path-deploy'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
        wait = true
    }
    deployTagCapistrano {
        className = 'com.adyax.wsip.commands.DeployTagCapistrano'
        requiredParams = ['tag', 'capRepo']
    }
    dbInfo {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-database-info'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
        noSimulate = true
    }
    dbAdd {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-database-add'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
    }
    domainInfo {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-domain-info'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
        noSimulate = true
    }
    domainAdd {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-domain-add'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
    }
    domainList {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-domain-list'
        requiredConfigParams = ['acquiaCloudDocrootName']
    }
    clearVarnishForURL {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-domain-purge'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
    }
    getAcquiaServers {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-server-list'
        requiredConfigParams = ['acquiaCloudDocrootName']
        noSimulate = true
    }
    waitCommandFinish {
        className = 'com.adyax.wsip.commands.AcquiaCloudCommand'
        acquiaCloudCommandName = 'ac-task-info'
        requiredConfigParams = ['acquiaCloudDocrootName']
        requiredParams = ['argument']
    }
    siteListAll {
        className = 'com.adyax.wsip.commands.SitesListAll'
        requiredConfigParams = ['sitesDir']
    }
    sitesListFromFile {
        className = 'com.adyax.wsip.commands.SitesListFromFile'
    }
    bulkCopySiteDB {
        className = 'com.adyax.wsip.commands.BulkCopySiteDB'
    }
    bulkCopySiteFiles {
        className = 'com.adyax.wsip.commands.BulkCopySiteFiles'
    }
    siteList {
        className = 'com.adyax.wsip.commands.SitesList'
    }
    siteListByProject {
        className = 'com.adyax.wsip.commands.SitesListProjectName'
        requiredParams = ['projectName']
        requiredConfigParams = ['sitesDir']
    }
    siteListByGitChangedFiles {
        className = 'com.adyax.wsip.commands.SitesListGitChangedFiles'
        requiredParams = ['projectName']
        skipList = ['info.yaml', 'CHANGES', 'VERSION', 'README', 'README.md', 'README.txt']
    }
    launch_jenkins_job {
        className = 'com.adyax.wsip.commands.LaunchJenkinsJob'
    }
    projectDirs {
        className = 'com.adyax.wsip.commands.ProjectsListBySite'
        subprojects = true
        requiredParams = ['site']
        requiredConfigParams = ['projectsDir']
    }
    drush {
        className = 'com.adyax.wsip.commands.Drush'
        requiredParams = ['command']
    }
    deployFlow {
        className = 'com.adyax.wsip.commands.Flow'
        flowName = 'deploy'
        flowType = 'deploy'
    }
    getContextDirs {
        className = 'com.adyax.wsip.commands.GetContextDirs'
        requiredParams = ['site']
    }
    getContextScripts {
        className = 'com.adyax.wsip.commands.GetContextScripts'
        requiredParams = ['site']
    }
    executeSteps {
        className = 'com.adyax.wsip.commands.ExecuteSteps'
    }
    shellCommand {
        className = 'com.adyax.wsip.commands.ShellCommand'
        requiredParams = ['cmd', 'dir']
        logTime = true
    }
    sitesSymlinksInGit {
        className = 'com.adyax.wsip.commands.SitesSymlinksInGit'
    }
    sitesSymlinks {
        className = 'com.adyax.wsip.commands.SitesSymlinks'
    }
    siteSymlinks {
        className = 'com.adyax.wsip.commands.SiteSymlinks'
        requiredParams = ['site']
    }
    drupalCoreUpdate {
        className = 'com.adyax.wsip.commands.DirUpdate'
        preservedFiles = ['sites', '*.txt', '.htaccess', '.gitignore']
        prepareSourceCommand = 'downloadDrupal'
        prepareTargetCommand = 'gitGetRepo'
        writeResultToProperties = true
    }
    gitGetRepo {
        className = 'com.adyax.wsip.commands.GitGetRepo'
        requiredParams = ['repoAddress', 'branch']
    }
    gitPushRepo {
        className = 'com.adyax.wsip.commands.GitPushRepo'
        requiredParams = ['repoDir', 'branch']
    }
    gitRepoChanged {
        className = 'com.adyax.wsip.commands.GitRepoChanged'
        requiredParams = ['repoDir']
    }
    downloadDrupal {
        className = 'com.adyax.wsip.commands.DownloadDrupal'
        noSimulate = true
    }
    drushFeaturesList {
        className = 'com.adyax.wsip.commands.DrushFeaturesList'
        noSimulate = true
        writeResultToProperties = true
    }
    drushUPDB {
        className = 'com.adyax.wsip.commands.DrushUPDB'
    }
    executeContextCommandFromGit {
        className = 'com.adyax.wsip.commands.ExecuteContextCommandFromGit'
    }
    executeContextCommand {
        className = 'com.adyax.wsip.commands.ExecuteContextCommand'
    }
    addSite {
        className = 'com.adyax.wsip.commands.AddSite'
    }
    processSites {
        className = 'com.adyax.wsip.commands.ProcessSites'
        urlEnvNames = ['dev', 'test', 'prod']
    }
    clearVarnish {
        className = 'com.adyax.wsip.commands.ClearVarnish'
        mode = 'acquia-purge'
    }
    clearVarnishAll {
        className = 'com.adyax.wsip.commands.ClearVarnish'
        mode = 'all'
        site = 'default'
    }
    copySite {
        className = 'com.adyax.wsip.commands.CopySite'
        requiredParams = ['env', 'site', 'from']
    }
    copySiteDB {
        className = 'com.adyax.wsip.commands.CopySiteDB'
        requiredParams = ['env', 'site', 'from']
    }
    copySiteFiles {
        className = 'com.adyax.wsip.commands.CopySiteFiles'
        requiredParams = ['env', 'site', 'from']
    }
    drupalVersionChecker {
        className = 'com.adyax.wsip.commands.DrupalVersionChecker'
        requiredParams = ['status']
        noSimulate = true
        writeResultToProperties = true
    }
    bumpStable {
        className = 'com.adyax.wsip.commands.ExecuteCommandInGitDir'
        cmd = 'docman bump stable -n'
    }
}

environments {
    jenkins {
        paramsLoader = 'com.adyax.wsip.JenkinsParamsLoader'
    }
    local {
        paramsLoader = 'com.adyax.wsip.LocalParamsLoader'
    }
}
