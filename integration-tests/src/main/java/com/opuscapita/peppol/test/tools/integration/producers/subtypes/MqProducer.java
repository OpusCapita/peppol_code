package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class MqProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(Producer.class);
    private String sourceFileName;
    private String destinationQueue;

    public MqProducer(String sourceFileName, String destinationQueue) {
        this.sourceFileName = sourceFileName;
        this.destinationQueue = destinationQueue;
    }

    @Override
    public void run() {
        try {
            File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                logger.error(this.sourceFileName + " doesn't exist!");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error running MqProducer!", ex);
        }
    }
}
