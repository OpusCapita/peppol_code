package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileSubscriber extends Subscriber {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileSubscriber.class);
    private final String sourceFile;

    public FileSubscriber(Object sourceFile, Object timeout) {
        super(timeout);
        this.sourceFile = (String) sourceFile;
    }

    @Override
    public void run() {
        File file = null;
        try {
            file = new File(sourceFile);
            if (!file.isFile()) {
                logger.error(this.sourceFile + " doesn't exist!");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceFile, ex);
            return;
        }
        for (Consumer consumer : consumers) {
            consumer.consume(sourceFile);
        }
    }
}
