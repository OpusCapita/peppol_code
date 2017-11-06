package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileSubscriber extends Subscriber {
    private final static Logger logger = LoggerFactory.getLogger(FileSubscriber.class);
    private final String sourceFile;

    public FileSubscriber(Object timeout, Object sourceFile) {
        super(timeout);
        this.sourceFile = (String) sourceFile;
    }

    //peppol/data/storage/ for the support ui on stage
    @Override
    protected void fetchConsumable() {
        File file;
        try {
            file = new File(sourceFile);
            if (!file.exists()) {
                logger.warn(this.sourceFile + " not found!");
            } else {
                consumable = sourceFile;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceFile, ex);
        }
    }
}
