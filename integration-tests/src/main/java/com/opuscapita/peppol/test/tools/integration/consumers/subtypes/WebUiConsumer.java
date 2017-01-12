package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

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
        //Properties properties = new Properties();
        //properties.load(new FileInputStream(directory + "\\webUiResult"));
        return new TestResult("", false, "WebUiConsumer not implemented yet");
    }
}
