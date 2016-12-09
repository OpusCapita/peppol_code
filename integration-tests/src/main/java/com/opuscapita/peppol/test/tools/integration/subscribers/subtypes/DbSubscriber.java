package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.util.StorageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class DbSubscriber extends Subscriber {
    @Autowired
    StorageService storageService;
    public DbSubscriber(Object timeout) {
        super(timeout);
    }

    @Override
    public void run() {
        //select all table , JSON and pass to subscribers
    }
}
