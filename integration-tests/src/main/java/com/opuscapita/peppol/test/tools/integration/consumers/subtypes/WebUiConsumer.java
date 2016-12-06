package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.12.02..
 */
public class WebUiConsumer implements Consumer {
    private final String directory;
    private final boolean expectedResult;

    public WebUiConsumer(String directory, boolean expectedResult) {
        this.directory = directory;
        this.expectedResult = expectedResult;
    }
    //Properties properties = new Properties();
    //properties.load(new FileInputStream(directory + "\\webUiResult"));


    @Override
    public boolean isDone() {
        return false;
    }
}
