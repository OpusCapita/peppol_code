package com.opuscapita.peppol.tests.mq;

//import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Should be able to work without spring context due to limitations of the inbound module.
 *
 * @author Sergejs.Roze
 */
@Component
public class RabbitMq {

    public void send(
            @NotNull MqProperties mqProperties, @Nullable String exchange, @NotNull String queueName, @NotNull String message)
            throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = getConnection(mqProperties);
            channel = connection.createChannel();

            channel.queueDeclare(queueName, true, false, false, null); // maybe not the best set of options
            channel.basicPublish(exchange == null ? "" : exchange, queueName, null, message.getBytes());

        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    protected Connection getConnection(MqProperties mqProperties) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(mqProperties.getHost());
        if (mqProperties.getPort() != 0) {
            factory.setPort(mqProperties.getPort());
        }
        if (mqProperties.getUserName() != null) {
            factory.setUsername(mqProperties.getUserName());
            factory.setPassword(mqProperties.getPassword());
        }

        return factory.newConnection();
    }

}
