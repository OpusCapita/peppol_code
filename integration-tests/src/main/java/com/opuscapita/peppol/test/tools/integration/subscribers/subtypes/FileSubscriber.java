package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

/**
 * Created by gamanse1 on 2016.11.24..
 */
public class FileSubscriber extends Subscriber {

    private final String sourceFolder;

    public FileSubscriber(Object sourceFolder, Object timeout) {
        super(timeout);
        this.sourceFolder = (String) sourceFolder;
    }
}
