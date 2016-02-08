/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import com.adyax.wsip.ContextScript

class GetContextScripts extends Command {

    String site

    String type

    String subdir

    def defaultContextDirs

    def perform() {
        def scripts = []
        getContextDirs(site: site, defaultContextDirs: defaultContextDirs).each { contextDir ->
            contextDir.getFiles(type, subdir).each { file ->
                scripts << new ContextScript(context: contextDir.context, script: file, type: type)
            }
        }
        scripts
    }

}
