package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class MqProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(Producer.class);
    private String sourceFolder;
    private String destinationQueue;

    public MqProducer(String sourceFolder, String destinationQueue) {
        this.sourceFolder = sourceFolder;
        this.destinationQueue = destinationQueue;
    }

    @Override
    public void run() {
        try {
            File directory = new File(sourceFolder);
            if (!directory.isDirectory()) {
                logger.error(this.sourceFolder + " doesn't exist!");
                return;
            }


        } catch (Exception ex) {
            logger.error("Error running MqProducer!", ex);
        }
    }
}
