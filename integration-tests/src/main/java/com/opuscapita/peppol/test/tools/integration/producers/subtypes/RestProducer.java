package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class RestProducer implements Producer{
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(RestProducer.class);
    private final String sourceDirectory;
    private final String template;
    private final String endpoint;
    private final String method;

    public RestProducer(Object sourceDirectory, Object template, Object endpoint, Object method) {
        this.sourceDirectory = (String) sourceDirectory;
        this.template = (String) template;
        this.endpoint = (String) endpoint;
        this.method = (String) method;
    }

    @Override
    public void run() {
        File directory = null;
        try {
            directory = new File(sourceDirectory);
            if (!directory.isDirectory()) {
                logger.error(this.sourceDirectory + " doesn't exist!");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceDirectory, ex);
            return;
        }


    }
}
