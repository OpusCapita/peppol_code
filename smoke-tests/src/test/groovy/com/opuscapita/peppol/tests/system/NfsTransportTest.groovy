//-------------------------------------------------
//
//This test:
//  1. remove specific row from DB table "files"
//  2. place file from local directory to remote directory
//  3. search DB for specific file
//  4. assert if expected file is in DB
//
//-------------------------------------------------
package com.opuscapita.peppol.tests.system

import com.opuscapita.peppol.tests.BaseTest
import org.junit.Test
import org.ysb33r.groovy.dsl.vfs.VFS

import static com.aestasit.infrastructure.ssh.DefaultSsh.remoteSession
import static com.aestasit.infrastructure.ssh.DefaultSsh.setTrustUnknownHosts

class NfsTransportTest extends BaseTest {

    @Test
    void ftp() {
        def vfs = new VFS()
        setTrustUnknownHosts(true)
        remoteSession(remoteUrl) {
            vfs.cp(
                    new File('C:\\Tests_local\\FTP_in\\berzima1_test.txt'),
                    "file://s-l-web1.ocnet.local:8080/home/elpp/berzima1/test/file_in/testIn.txt"
            )
        }
    }


    static String getRemoteUrl() {
        "${integrationProperties.getProperty('remoteSshUser')}:${integrationProperties.getProperty('remoteSshPass')}@${integrationProperties.getProperty('remoteSshHost')}:22"
    }
}
