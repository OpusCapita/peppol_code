package com.opuscapita.peppol.support.ui.components;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.7.2
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public interface RabbitMqConnectionPool {

    public RabbitTemplate getRabbitTemplate(String host, int port, String queueName);

    public RabbitAdmin getRabbitAdmin(String host, int port, String queueName);
}
