/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.ContextDir
import com.adyax.wsip.utils.Common

class GetContextDirs extends Command {

    String site

    def defaultContextDirs

    def perform() {
        def contextDirs = []
        defaultContextDirs.each { contextDir ->
            contextDirs << new ContextDir(context: retrieveContext(contextDir.contextName), dir: contextDir.dir)
        }
        projectDirs(site: site).each { dir ->
            dir -= (docrootPrefixDir() + '/')
            contextDirs << new ContextDir(context: config.contexts['project'], dir: dir)
        }

        contextDirs
    }

}
