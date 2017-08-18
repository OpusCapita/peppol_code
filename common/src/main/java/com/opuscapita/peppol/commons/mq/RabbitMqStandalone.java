package com.opuscapita.peppol.commons.mq;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Should be able to work without spring context due to limitations of the inbound module.
 *
 * @author Sergejs.Roze
 */
public class RabbitMqStandalone implements MessageQueue {
    private final MqProperties properties;
    private final ContainerMessageSerializer serializer = new ContainerMessageSerializer();

    public RabbitMqStandalone(@NotNull MqProperties properties) {
        this.properties = properties;
    }

    @Override
    public void convertAndSend(@NotNull String connectionString, @NotNull ContainerMessage message)
            throws IOException, TimeoutException {
        Connection connection = null;
        Channel channel = null;
        try {
            connection = getConnection(properties);
            channel = connection.createChannel();

            ConnectionString cs = new ConnectionString(connectionString);

            String queue = cs.getQueue();
            String exchange = cs.getExchange();

            channel.queueDeclare(queue, true, false, false, null); // maybe not the best set of options
            channel.basicPublish(exchange == null ? "" : exchange, queue, null, serializer.toJson(message).getBytes());
        } finally {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Connection getConnection(MqProperties mqProperties) throws IOException, TimeoutException {
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
