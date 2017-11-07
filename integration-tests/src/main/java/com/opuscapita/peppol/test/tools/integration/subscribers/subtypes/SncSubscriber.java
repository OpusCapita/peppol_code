package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class SncSubscriber extends Subscriber {
    private final String sourceDirectory;

    public SncSubscriber(Object timeout, Object sourceDirectory) {
        super(timeout);
        this.sourceDirectory = (String) sourceDirectory;
    }

    @Override
    protected void fetchConsumable() {
        if (sourceDirectory != null && !sourceDirectory.isEmpty()) {
            consumable = new File(sourceDirectory);
        }
    }
}
