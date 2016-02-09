/**
 * Created by alex on 28.06.15.
 */

package com.adyax.wsip.commands

import groovy.json.JsonSlurper

class DrushFeaturesList extends Command {

    String state = 'Overridden'

    def perform() {
        def features = []
        String output = drush command: "fl --format=json", noSimulate: noSimulate
        output = output.substring(output.indexOf('[{'))
        def jsonOutput = new JsonSlurper().parseText(output)
        jsonOutput.each { feature ->
            if (feature['State'] == state) {
                features << feature
            }
        }
        features
    }

}
