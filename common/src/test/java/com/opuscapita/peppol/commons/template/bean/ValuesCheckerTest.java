package com.opuscapita.peppol.commons.template.bean;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Sergejs.Roze
 */
public class ValuesCheckerTest {
    private static class Tester extends ValuesChecker {
        @FileMustExist
        private String fileName;

        void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    private static Tester tester = new Tester();

    @BeforeClass
    public static void before() throws IOException {
        File tmp = File.createTempFile("unit-test-", "-remove.me");
        tmp.deleteOnExit();
        tester.setFileName(tmp.getAbsolutePath());
    }

    @Test
    public void fileMustExistCheck() throws IllegalAccessException {
        // no exception expected
        tester.checkValues();

        // and now the must be an exception
        tester.setFileName("WOtNoSuChFiLe");
        try {
            tester.checkValues();
            fail("Expected exception not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("WOtNoSuChFiLe"));
        }
    }

}