package com.opuscapita.peppol.commons.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Sergejs.Roze
 */
@Component
public class RabbitMq implements MessageQueue {

    @Override
    public void send(@NotNull MqProperties mqProperties, @NotNull String queueName, @NotNull String message)
            throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(mqProperties.getHost());
        if (mqProperties.getPort() != 0) {
            factory.setPort(mqProperties.getPort());
        }
        if (mqProperties.getUserName() != null) {
            factory.setUsername(mqProperties.getUserName());
            factory.setPassword(mqProperties.getPassword());
        }

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(queueName, true, false, false, null); // maybe not the best set of options
        channel.basicPublish("", queueName, null, message.getBytes());

        channel.close();
        connection.close();
    }

}
