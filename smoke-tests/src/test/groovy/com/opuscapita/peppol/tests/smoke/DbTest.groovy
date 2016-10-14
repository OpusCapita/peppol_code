package com.opuscapita.peppol.tests.smoke

import com.opuscapita.peppol.tests.BaseTest
import groovy.sql.Sql
import org.junit.After
import org.junit.Before
import org.junit.Test

class DbTest extends BaseTest {
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
    @Test //test if DB is accessible
    void pingDB() {
        def ping = database.firstRow('select 1')
        println(ping[0])
        assert ping[0] == 1
    }
}

