package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.rabbitmq.client.Channel;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class MqSubscriber extends Subscriber {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(MqSubscriber.class);
    private final String queue;
    private Map<String, String> mqSettings;
    private final Gson gson = ContainerMessage.prepareGson();

    public MqSubscriber(Object timeout, Map<String, String> mqSettings, Object queue) {
        super(timeout);
        this.mqSettings = mqSettings;
        this.queue = (String) queue;
    }

    @Override
    public List<TestResult> run() {
        logger.info("MqSubscriber: started!");
        logger.info("Not implemented yet");
        //TODO get messages from MessageListener ?????
        Channel channel = null;
        return testResults;
    }
}
