package com.adyax.wsip.utils

/**
 * Created by alex on 29.10.15.
 */
class PropertiesHandler {

    File propFile

    String defaultPropertyName = 'result'

    def writeProperties(properties) {
        Properties props = new Properties()

        if (properties instanceof LinkedHashMap) {
            properties.each { property ->
                props.setProperty(property.key, property.value.toString())
            }
        }
        else {
            props.setProperty(defaultPropertyName, properties.toString())
        }

        props.store(propFile.newWriter(), null)
    }
}

