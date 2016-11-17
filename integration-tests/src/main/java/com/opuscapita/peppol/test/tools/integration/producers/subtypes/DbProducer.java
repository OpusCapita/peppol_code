package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbProducer implements Producer {
    private String query;

    public DbProducer(String query) {
        this.query = query;
    }
}
