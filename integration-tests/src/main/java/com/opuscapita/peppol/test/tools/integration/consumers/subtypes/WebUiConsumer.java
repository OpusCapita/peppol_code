package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.12.02..
 */
public class WebUiConsumer extends Consumer {
    private final int expectedResult;

    public WebUiConsumer(String id, Object expectedResult) {
        super(id);
        this.expectedResult = (int) expectedResult;
    }
    //Properties properties = new Properties();
    //properties.load(new FileInputStream(directory + "\\webUiResult"));


    @Override
    public boolean isDone() {
        return false;
    }
}
