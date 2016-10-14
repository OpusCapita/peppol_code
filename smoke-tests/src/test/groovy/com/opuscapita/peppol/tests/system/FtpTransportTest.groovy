//-------------------------------------------------
//
//This test:
//  1. remove specific row from DB table "files"
//  2. place file from local directory to remote FTP directory
//  3. search DB for specific file
//  4. assert if expected file is in DB
//
//-------------------------------------------------
package com.opuscapita.peppol.tests.system

import com.opuscapita.peppol.tests.BaseTest
import groovy.sql.Sql
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.ysb33r.groovy.dsl.vfs.VFS

class FtpTransportTest extends BaseTest {
    Sql database

    @Before
    void createDatabase() {
        database = Sql.newInstance(
                url: integrationProperties['remoteDbZebraUrl'],
                user: integrationProperties['remoteDbZebraUser'],
                password: integrationProperties['remoteDbZebraPass'],
                driver: integrationProperties['remoteDbZebraDriver']
        )
    }

    @After
    void disconnectDatabase() {
        database.close()
        database = null
    }
    @Test
    void ftp() {
        def vfs = new VFS()
        def username = integrationProperties['remoteFTPuser']
        def password = integrationProperties['remoteFTPpass']
        //enable when have test data in DB!!!
        //def files = database.firstRow('delete from files where filename="4123-000250577187.xml"')
        vfs.cp(
                new File('C:\\Tests_local\\FTP_in\\ehf_2.0_bii4_no.xml'),
                "ftp://${username}:${password}@ftp.itella.net/validation/test/in/berzima1.test"
        )
        def files = database.firstRow('select filename from files where filename="4123-000250577187.xml"')
        println(files)
        assert files == ["filename":"4123-000250577187.xml"]
    }

}
