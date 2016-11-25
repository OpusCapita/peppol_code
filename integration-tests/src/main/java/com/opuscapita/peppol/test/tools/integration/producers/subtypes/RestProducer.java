package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class RestProducer implements Producer{
    private final String file;
    private final String template;
    private final String endpoint;
    private final String method;

    public RestProducer(Object file, Object template, Object endpoint, Object method) {
        this.file = (String) file;
        this.template = (String) template;
        this.endpoint = (String) endpoint;
        this.method = (String) method;
    }

    @Override
    public void run() {

    }
}
