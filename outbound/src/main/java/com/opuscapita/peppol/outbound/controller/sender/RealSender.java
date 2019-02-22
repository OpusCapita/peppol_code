package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.outbound.Transmitter;
import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
public class RealSender implements PeppolSender {

    private static Logger logger = LoggerFactory.getLogger(RealSender.class);

    private OxalisOutboundComponent oxalisOutboundModule;

    public RealSender() {
        this.oxalisOutboundModule = new OxalisOutboundComponent();
    }

    OxalisOutboundComponent getOxalisOutboundModule() {
        return oxalisOutboundModule;
    }

    TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisOutboundModule.getTransmissionRequestBuilder();
    }

    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException, OxalisContentException, OxalisTransmissionException {
        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequest transmissionRequest = getTransmissionRequestBuilder().payLoad(inputStream).build();

            logger.info("Thread " + Thread.currentThread().getName() + " is about to send message: " + cm.toLog() + " to endpoint: " + transmissionRequest.getEndpoint());

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();
            return transmitter.transmit(transmissionRequest);
        }
    }
}