package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbConsumer extends Consumer {
    private final String connectionString;
    private final String name;
    private String query;
    private Object expectedValue;

    public DbConsumer(String id, String consumerName, String dbConnectionString, String query, Object expectedValue) {
        super(id);
        this.name = consumerName;
        this.connectionString = dbConnectionString;
        this.query = query;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void consume(String consumable) {

    }
}
