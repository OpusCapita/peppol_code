package com.opuscapita.peppol.tests.smoke

import com.opuscapita.peppol.tests.BaseTest
import org.junit.Test

class SupportUITest extends BaseTest {

    @Test
    void pingUI() {
        URL u = new URL("http://peppol.itella.net/login")
        def connection = u.openConnection() as HttpURLConnection
        connection.requestMethod = "HEAD"
        assert connection.responseCode == 405
    }

}

