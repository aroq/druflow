package com.adyax.wsip

import com.adyax.wsip.utils.Common

/**
 * Created by alex on 09.08.15.
 */
class ContextDir {
    String dir
    def context

    @Delegate Common commonUtils = Common.instance

    def getFiles(type, dirToSearch) {
//        dirToSearch = new File(docrootPrefixDir() + '/' + dir + '/' + dirToSearch)
        dirToSearch = new File(dir + '/' + dirToSearch)
        def pattern = ".*${type}\\.flow\\.steps"
        def files = []
        if (dirToSearch.exists()) {
            dirToSearch.eachFileMatch(~/${pattern}/) { script ->
                files << script
            }
        }
        files
    }

}
