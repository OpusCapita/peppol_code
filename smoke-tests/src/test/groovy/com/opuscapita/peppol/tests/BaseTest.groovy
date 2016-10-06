package com.opuscapita.peppol.tests

abstract class BaseTest {

    static Properties getIntegrationProperties() {
        File file = new File('local.properties')
        def props = System.properties
        if (file.exists()) {
            props = new Properties()
            props.load(file.newInputStream())
        }
        props
    }

}
