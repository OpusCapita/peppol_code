package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class MqProducer implements Producer{
    private String sourceFile;
    private String destinationQueue;

    public MqProducer(String sourceFile, String destinationQueue) {
        this.sourceFile = sourceFile;
        this.destinationQueue = destinationQueue;
    }
}
