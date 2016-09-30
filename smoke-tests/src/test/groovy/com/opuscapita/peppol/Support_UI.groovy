package com.opuscapita.peppol

import geb.Browser
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

public class SupportUItest {

//    @BeforeClass
//    static void setUp() {
//        println "BeforeClass - Test it"
//    }

//    @Before
//    void setUp2() {
//        println "Test it"
//    }

//    @Test
//    void smokeTest11() {
//        throw new RuntimeException()
//    }

    @Test
    void PingUI() {
        HttpURLConnection connection = null;
        try {
            URL u = new URL("http://peppol.itella.net/login");
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            int code = connection.getResponseCode();
            System.out.println("smokePing answer: " + code);
            // You can determine on HTTP return code received. 200 is success.
            assert code == 405
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

