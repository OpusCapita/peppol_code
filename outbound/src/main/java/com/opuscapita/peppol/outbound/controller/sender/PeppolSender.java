package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Common interface for all senders.
 *
 * @author Sergejs.Roze
 */
@FunctionalInterface
public interface PeppolSender {

    TransmissionResponse send(@NotNull ContainerMessage containerMessage) throws IOException, OxalisContentException, OxalisTransmissionException;

}
