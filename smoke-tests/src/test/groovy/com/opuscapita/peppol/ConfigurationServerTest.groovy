package com.opuscapita.peppol

import groovy.json.JsonSlurper
import org.junit.Test

import static com.aestasit.infrastructure.ssh.DefaultSsh.remoteSession
import static com.aestasit.infrastructure.ssh.DefaultSsh.setTrustUnknownHosts
import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson

class ConfigurationServerTest extends BaseTest {

    @Test
    void pingConfigurationServer() {
        setTrustUnknownHosts(true)
        remoteSession(remoteUrl) {
            tunnel('s-l-web1.ocnet.local', 8888) { int localPort ->
                def response = new JsonSlurper().parse(new URL("http://localhost:${localPort}/events-persistence/info").bytes)
                println prettyPrint(toJson(response))
                assert response.name == 'events-persistence'
                assert response.profiles == ['info']
                assert response.label == 'master'
                assert response.propertySources[0].name.contains('nocontrol')
            }
        }
    }

    static String getRemoteUrl() {
        "${integrationProperties.getProperty('remoteSshUser')}:${integrationProperties.getProperty('remoteSshPass')}@${integrationProperties.getProperty('remoteSshHost')}:22"
    }

}
