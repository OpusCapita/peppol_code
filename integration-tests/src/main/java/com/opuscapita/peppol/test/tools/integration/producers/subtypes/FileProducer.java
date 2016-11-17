package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class FileProducer implements Producer {
    private String sourceFile;
    private String destinationFile;

    public FileProducer(String sourceFile, String destinationFile) {
        this.sourceFile = sourceFile;
        this.destinationFile = destinationFile;
    }
}
