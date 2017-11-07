package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by gamanse1 on 2016.12.09..
 * Reads file got from rest validation, it's content contains validation result in json one line per test file:
 * {"validationType":"UBL","passed":true,"errors":[]}
 */
public class RestConsumer extends Consumer {

    private final int expectedResult;

    public RestConsumer(String id, String name, Object expectedResult) {
        super(id);
        this.name = name;
        this.expectedResult = (int) expectedResult;
    }

    @Override
    public TestResult consume(Object consumable) {
        int passed = 0;
        List<String> linedResult;
        try {
            ValidationResult result;
            linedResult = Files.readLines(new File((String)consumable), Charsets.UTF_8);
            for(String line : linedResult){
                result = new Gson().fromJson(line, ValidationResult.class);
                if(result.isPassed())
                    passed ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TestResult(name, passed == expectedResult, "Expected: "+ expectedResult +" Got: " + passed);
    }
}
