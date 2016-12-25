package com.opuscapita.peppol.commons.mq;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Common interface for all communications with MQ.
 *
 * @author Sergejs.Roze
 */
public interface MessageQueue {
    /**
     * Connect and send a string message.
     * Connection string format is:<br/><code>
     *     queue_name:parameter1=value1,parameter2,parameter3=value3
     * </code><br/>
     * Where known parameters are:
     * <ul>
     *     <li>exchange=name - name of the exchange to use</li>
     *     <li>x-delay=n - will put a header to the message that is recognizable by delayed queue, delays message for n milliseconds</li>
     * </ul><br/>
     * Without parameters simply represents the name of the queue to send to.
     *
     */
    void send(@NotNull String connectionString, @NotNull ContainerMessage message) throws IOException, TimeoutException;

}
