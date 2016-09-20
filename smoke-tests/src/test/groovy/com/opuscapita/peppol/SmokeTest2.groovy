package com.opuscapita.peppol

import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

public class SmokeTest2 {

    @BeforeClass
    static void setUp() {
        println "Test it"
    }

    @Before
    void setUp2() {
        println "Test it"
    }

    @Test
    void smokeTest11() {
        throw new RuntimeException()
    }

    @Test
    void smokeTest12() {
        println "Test it"
    }

}
