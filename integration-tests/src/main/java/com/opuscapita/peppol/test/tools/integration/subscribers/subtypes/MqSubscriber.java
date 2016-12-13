package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;

import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class MqSubscriber extends Subscriber {

    private final String queue;
    private Map<String, String> mqSettings;

    public MqSubscriber(Object timeout, Map<String, String> mqSettings, Object queue) {
        super(timeout);
        this.mqSettings = mqSettings;
        this.queue = (String) queue;
    }

    @Override
    public void run() {

    }
}
