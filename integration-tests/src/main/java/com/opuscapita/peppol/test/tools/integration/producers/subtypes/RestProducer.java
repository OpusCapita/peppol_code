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

    public RestProducer(String file, String template, String endpoint, String method) {
        this.file = file;
        this.template = template;
        this.endpoint = endpoint;
        this.method = method;
    }
}
