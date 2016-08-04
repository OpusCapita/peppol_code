package com.opuscapita.peppol.validator.validations.difi;

import no.difi.vefa.validator.api.ValidatorException;
import org.junit.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by Daniil on 03.05.2016.
 */
public class DifiValidatorTest {
    private static DifiValidator difiValidator;

    @BeforeClass
    public static void init() {
        try {
            difiValidator = new DifiValidator();
        } catch (ValidatorException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void shutdown() {
        difiValidator = null;
    }

    @Test
    public void validateNull() throws Exception{
        boolean result = difiValidator.validate(null);
        assertFalse(result);
        difiValidator.getErrors().forEach(error -> System.out.println(error));
    }

    @Test
    public void validateEmpty() throws Exception {
        boolean result = difiValidator.validate("".getBytes());
        difiValidator.getErrors().forEach(error -> System.out.println(error));
        assertFalse(result);

    }

    @Test
    public void testValidFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(DifiValidatorTest.class.getResourceAsStream("/SAMPLE_INVOICE.xml")));
        byte[] data = reader.lines().collect(Collectors.joining()).getBytes();
        boolean result = difiValidator.validate(data);
        difiValidator.getErrors().forEach(error -> System.out.println(error));
        assertTrue(result);
    }


}