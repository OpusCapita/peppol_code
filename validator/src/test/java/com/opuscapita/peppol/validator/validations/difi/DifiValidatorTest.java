package com.opuscapita.peppol.validator.validations.difi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Daniil on 03.05.2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DifiValidatorTest {
    @Autowired
    DifiValidator difiValidator;

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