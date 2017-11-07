package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.12.02..
 */
public class WebUiConsumer extends Consumer {
    private final int expectedResult;

    public WebUiConsumer(String id, String name, Object expectedResult) {
        super(id);
        this.name = name;
        this.expectedResult = (int) expectedResult;
    }

    @Override
    public TestResult consume(Object consumable) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream((String) consumable));
        } catch (IOException e) {
            e.printStackTrace();
            return new TestResult(name, false, "Exception while loading: "+ consumable);
        }
        int goodResults = 0;
        for (Object result: properties.values()) {
            if("true".equals(result))
                goodResults++;
        }
        boolean passed = goodResults == expectedResult;
        return new TestResult(name, passed, "Expected: "+ expectedResult +" Got: " + goodResults);
    }
}
