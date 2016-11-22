package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbConsumer implements Consumer {
    private final String connectionString;
    private Object expectedValue;

    public DbConsumer(String dbConnectionString, Object expectedValue) {
        this.connectionString = dbConnectionString;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
