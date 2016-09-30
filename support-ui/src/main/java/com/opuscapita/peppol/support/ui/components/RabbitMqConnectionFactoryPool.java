package com.opuscapita.peppol.support.ui.components;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.6.2
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */

@Component("rabbitMQPool")
public class RabbitMqConnectionFactoryPool implements RabbitMqConnectionPool {

    private Map<String, ConnectionFactory> connectionFactoryMap;
    private Map<String, RabbitTemplate> rabbitTemplateMap;
    private Map<String, RabbitAdmin> rabbitAdminMap;

    public RabbitMqConnectionFactoryPool() {
        rabbitTemplateMap = new HashMap<>();
        rabbitAdminMap = new HashMap<>();
        connectionFactoryMap = new HashMap<>();
    }

    public ConnectionFactory getConnectionFactory(String host, int port) {
        String key = host + ":" + port;
        ConnectionFactory cf = connectionFactoryMap.get(key);
        if (cf != null) {
            return cf;
        }
        cf = new CachingConnectionFactory(host, port);
        connectionFactoryMap.put(key, cf);
        return cf;
    }

    @Override
    public RabbitTemplate getRabbitTemplate(String host, int port, String queueName) {
        String key = host + ":" + port;
        RabbitTemplate rabbitTemplate = rabbitTemplateMap.get(key);
        if (rabbitTemplate != null) {
            return rabbitTemplate;
        }

        RabbitAdmin rabbitAdmin = getRabbitAdmin(host, port, queueName);
        // create template and put in map
        rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        rabbitTemplateMap.put(key, rabbitTemplate);
        return rabbitTemplate;
    }

    @Override
    public RabbitAdmin getRabbitAdmin(String host, int port, String queueName) {
        String key = host + ":" + port;
        RabbitAdmin rabbitAdmin = rabbitAdminMap.get(key);
        if (rabbitAdmin != null) {
            return rabbitAdmin;
        }
        ConnectionFactory cf = getConnectionFactory(host, port);
        rabbitAdmin = new RabbitAdmin(cf);
        Queue queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        rabbitAdminMap.put(key, rabbitAdmin);
        return rabbitAdmin;
    }

}
