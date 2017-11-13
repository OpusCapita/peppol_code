package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import eu.peppol.outbound.transmission.TransmissionResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Common interface for all senders.
 *
 * @author Sergejs.Roze
 */
@FunctionalInterface
public interface PeppolSender {

    CompletableFuture<TransmissionResponse> send(@NotNull ContainerMessage containerMessage) throws IOException;

}
