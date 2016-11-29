package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.29..
 */
public class WebUiProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(WebUiProducer.class);
    private String sourceDirectory;

    public WebUiProducer(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
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
