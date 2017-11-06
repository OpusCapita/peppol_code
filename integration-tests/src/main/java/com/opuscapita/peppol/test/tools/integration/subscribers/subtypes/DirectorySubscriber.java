package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

import java.io.File;

public class DirectorySubscriber extends Subscriber {
    private final String sourceDirectory;

    public DirectorySubscriber(Object timeout, Object sourceDirectory) {
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
