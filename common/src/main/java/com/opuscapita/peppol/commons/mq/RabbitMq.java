package com.opuscapita.peppol.commons.mq;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Sergejs.Roze
 */
@Component
public class RabbitMq implements MessageQueue {
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMq(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(@NotNull String connectionString, @NotNull ContainerMessage message)
            throws IOException, TimeoutException {
        ConnectionString cs = new ConnectionString(connectionString);

        String exchange = cs.getExchange();
        int delay = cs.getXDelay();

        if (exchange != null) {
            if (delay == 0) {
                // exchange defined, no delay
                rabbitTemplate.convertAndSend(exchange, cs.getQueue(), message);
            } else {
                // both exchange and delay are defined
                rabbitTemplate.convertAndSend(exchange, cs.getQueue(), message, m -> {
                    m.getMessageProperties().setDelay(delay);
                    return m;
                });
            }
        } else {
            if (delay == 0) {
                // no exchange, no delay
                rabbitTemplate.convertAndSend(cs.getQueue(), message);
            } else {
                // no exchange, delay defined
                rabbitTemplate.convertAndSend(cs.getQueue(), message, m -> {
                    m.getMessageProperties().setDelay(delay);
                    return m;
                });
            }
        }
    }

}
