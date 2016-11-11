package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.TransmissionRequest;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import eu.peppol.outbound.transmission.TransmissionResponse;
import eu.peppol.outbound.transmission.Transmitter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
public class UblSender {

    @NotNull
    TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        OxalisOutboundModule oxalisOutboundModule = new OxalisOutboundModule();
        TransmissionRequestBuilder requestBuilder = oxalisOutboundModule.getTransmissionRequestBuilder();
        BaseDocument document = cm.getBaseDocument();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            requestBuilder = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(PeppolProcessTypeId.valueOf(document.getProfileId()))
                    .sender(new ParticipantId(document.getSenderId()))
                    .receiver(new ParticipantId(document.getRecipientId()))
                    .trace(true)
                    .payLoad(inputStream);

            TransmissionRequest transmissionRequest = requestBuilder.build();

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            return transmitter.transmit(transmissionRequest);
        }
    }

}
