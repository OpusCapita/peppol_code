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

    public WebUiConsumer(String id, Object expectedResult) {
        super(id);
        this.expectedResult = (int) expectedResult;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream((String) consumable));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TestResult("", false, "WebUiConsumer not implemented yet");
    }
}
