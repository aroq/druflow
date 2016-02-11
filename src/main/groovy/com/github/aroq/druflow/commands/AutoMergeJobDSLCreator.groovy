package com.github.aroq.druflow.commands

import com.github.aroq.druflow.Branch
import com.github.aroq.groovycommon.Common

class AutoMergeJobDSLCreator extends JobDSLCreator {

    def prepareBranch(name, branch, parent) {
        def branches = []
        def autoMergeToParent = false
        if (branch.autoMergeToParent && branch.autoMergeToParent != null) {
            autoMergeToParent = true
        }
        def newBranch = new Branch(name: name, parent: parent, autoMergeToParent: autoMergeToParent)
        branches << newBranch
        if (branch.branches) {
            branches += processBranchWorkflow(branch.branches, newBranch)
        }
        branches
    }

    def processNamePattern(pattern, parent) {
        pattern.findAll(/\{([^\}]*)\}/).each {
            def matcher = it =~ /\{(.*)\[(.*)\]\}/
            if (matcher.matches()) {
                def paramName = matcher[0][1]
                def paramPart = matcher[0][2]
                if (paramName == 'parentName') {
                    def paramValue = parent.name.split('/')[paramPart.toInteger()]
                    pattern = pattern.replace(it, paramValue)
                }
            }
        }

        pattern
    }

    def processBranchWorkflow(processedBranches, parent = null) {
        def branches = []
        processedBranches.each { name, branch ->
            if (branch.namePattern) {
                def pattern = processNamePattern(branch.namePattern, parent)
                def branchesList = Common.instance._executeCommand('gitlabBranchesList', this, [pattern: pattern])
                branchesList?.each{
                    def mergeToBranch = it - "remotes/${componentConfig.remoteName}/"
                    branches += prepareBranch(mergeToBranch, branch, parent)
                }
            }
            else {
                branches += prepareBranch(name, branch, parent)
            }
        }
        branches
    }

    def perform() {
        def componentConfigFile = loadConfig()

        def slurper = new ConfigSlurper()

        componentConfig = slurper.parse(componentConfigFile)

        scriptObject.folder("${componentConfig.projectFolder}/${componentName}")

        def branches = processBranchWorkflow(componentConfig.branchWorkflow.branches)

        branches.unique { branch ->
            branch.name + branch.parentName
        }

        branches.each {
            if (it.name && it.parentName) {
                createJob(mergeFromBranch: it.parentName, mergeToBranch: it.name)
                if (it.autoMergeToParent) {
                    createJob(mergeFromBranch: it.name, mergeToBranch: it.parentName)
                }
            }
        }
    }

    @Override
    def createJob(params) {
        params.mergeFromBranch = params.mergeFromBranch.trim()
        params.mergeToBranch = params.mergeToBranch.trim()

        log "${params.mergeFromBranch} -> ${params.mergeToBranch}"

        def mergeToBranchNameProcessed = params.mergeToBranch.replaceAll('/', '-').replaceAll("\\*", 'all')
        def mergeFromBranchNameProcessed = params.mergeFromBranch.replaceAll('/', '-').replaceAll("\\s", '').replaceAll("\\*", 'all')

        scriptObject.job("${componentConfig.projectFolder}/${componentName}/${componentConfig.jobPrefix}${componentName}-${mergeFromBranchNameProcessed}-to-${mergeToBranchNameProcessed}") {
            scm {
                git {
                    remote {
                        name(componentConfig.remoteName)
                        url(componentConfig.gitURL)
                    }
                    branch("${componentConfig.remoteName}/${params.mergeFromBranch}")
                    mergeOptions {
                        // Sets the name of the branch to merge.
                        branch(params.mergeToBranch.trim())
                        // Sets the name of the repository that contains the branch to merge.
                        remote(componentConfig.remoteName)
                        // Selects the merge strategy.
                        strategy('default')
                    }
                }
                triggers {
                    scm('@hourly')
                }
            }

            publishers {
                git {
                    pushOnlyIfSuccess()
                    pushMerge(true)
                }
            }
        }
    }
}

