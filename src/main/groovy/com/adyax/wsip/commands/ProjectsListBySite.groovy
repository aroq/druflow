/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

class ProjectsListBySite extends Command {

    def dirToSearch
    def projects = []

    Boolean subprojects

    Boolean withoutProfiles = false

    String site

    def perform() {
        dirToSearch = dirToSearch ? dirToSearch : projectsDir()
        def projectVariants = projectNameVariants(site)
        projectVariants.each { project ->
            def projectPath = projectExists(project)
            if (projectPath) {
                pushProject(project, projectPath)
                assert 1 == 1
            }

            new File(dirToSearch).eachDirMatch(~/${project}/) { dir ->
                pushProject(dir.getName(), dir.getPath())
                assert 1 == 1
            }
        }

        // TODO: Provide dirs in a proper order (root projects first)
        projects.unique { a, b -> a <=> b }
    }

    def projectExists(project) {
        def projectDir = new File(projectDir(project))
        projectDir.exists() ? projectDir.getPath() : null
    }

    def pushProject(projectName, projectPath) {
        projectPath = projectPath.toString()
        if (!projects.contains(projectPath) && !(withoutProfiles && projectPath.find(/profiles/))) {
            projects << projectPath
            debug("Found project: ${projectName} at path: ${projectPath}")
            if (subprojects) {
                ['sites', 'projects'].each { subprojectDirName ->
                    def subprojectsPath = new File(dirToSearch + '/' + projectName + '/' + subprojectDirName)
                    if (subprojectsPath.exists()) {
                        projects += executeCommand(command, [site: site, dirToSearch: subprojectsPath.getPath(), withoutProfiles: withoutProfiles])
                    }
                }
            }
        }

    }

}
