package com.opuscapita.peppol.test.tools.integration.subscribers.subtypes;

import com.opuscapita.peppol.test.tools.integration.subscribers.Subscriber;
import com.opuscapita.peppol.test.tools.integration.util.MqListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by gamanse1 on 2016.11.17..
 */
public class MqSubscriber extends Subscriber implements MqListener {
    private final static Logger logger = LoggerFactory.getLogger(MqSubscriber.class);
    private final String queue;
    private List<Message> messages = new CopyOnWriteArrayList<>();

    public MqSubscriber(Object timeout, Object queue) {
        super(timeout);
        this.queue = (String) queue;
    }

    @Override
    protected void fetchConsumable() {
        logger.info(queue + " Listener started");
        if (messages.isEmpty()) {
            logger.info("MqSubscriber: message list empty, waiting for messages .....");
        } else {
            logger.info("MqSubscriber: messages found: " + messages.size());
            consumable = messages;
        }
    }

    /*
    * Messages got from AbstractRabbitListenerEndpoint
    * */
    @Override
    public void onMessage(Message message) {
        messages.add(message);
        logger.info("MqSubscriber (" + queue + ").onMessage() Total messages: " + messages.size());
    }

    @Override
    public String getConsumerQueue() {
        return queue;
    }
}
