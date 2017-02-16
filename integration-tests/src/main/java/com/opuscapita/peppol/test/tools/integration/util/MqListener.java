package com.opuscapita.peppol.test.tools.integration.util;

import org.springframework.amqp.core.Message;

/**
 * Created by GAMANSE1 on 2017.02.10..
 */
public interface MqListener {
    public void onMessage(Message message);

    String getConsumerQueue();
}
