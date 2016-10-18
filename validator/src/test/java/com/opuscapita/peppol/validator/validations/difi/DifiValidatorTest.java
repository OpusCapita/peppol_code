package com.opuscapita.peppol.validator.validations.difi;

import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import org.junit.Ignore;
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
@Ignore("Fails, Daniil to fix")
@SpringBootTest
public class DifiValidatorTest {
    @Autowired
    DifiValidator difiValidator;

    @Test
    public void validateNull() throws Exception{
        ValidationResult result = difiValidator.validate(null);
        assertFalse(result.isPassed());
        result.getErrors().forEach(error -> System.out.println(error));
    }

    @Test
    public void validateEmpty() throws Exception {
        ValidationResult result = difiValidator.validate("".getBytes());
        result.getErrors().forEach(error -> System.out.println(error));
        assertFalse(result.isPassed());

    }

    @Test
    public void testValidFile() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(DifiValidatorTest.class.getResourceAsStream("/SAMPLE_INVOICE.xml")));
        byte[] data = reader.lines().collect(Collectors.joining()).getBytes();
        ValidationResult result = difiValidator.validate(data);
        result.getErrors().forEach(error -> System.out.println(error));
        assertTrue(result.isPassed());
    }


}