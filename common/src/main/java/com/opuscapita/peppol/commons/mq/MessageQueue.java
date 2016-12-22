package com.opuscapita.peppol.commons.mq;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Common interface for all communications with MQ.
 *
 * @author Sergejs.Roze
 */
public interface MessageQueue {
    /**
     * Connect to the queue and send a string message. Connects and disconnects for every message so is not efficient.
     */
    void send(@Nullable String exchange, @NotNull String queueName, @NotNull ContainerMessage message)
            throws IOException, TimeoutException;

}
