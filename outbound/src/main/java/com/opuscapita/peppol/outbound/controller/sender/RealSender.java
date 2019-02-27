package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.outbound.Transmitter;
import no.difi.oxalis.outbound.OxalisOutboundComponent;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.apache.commons.lang3.StringUtils;
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
        TransmissionRequestBuilder transmissionRequestBuilder = oxalisOutboundModule.getTransmissionRequestBuilder();
        transmissionRequestBuilder.setTransmissionBuilderOverride(true);
        return transmissionRequestBuilder;
    }

    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException, OxalisContentException, OxalisTransmissionException {
        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequestBuilder transmissionRequestBuilder = getTransmissionRequestBuilder()
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(cm))
                    .processType(OxalisUtils.getPeppolProcessTypeId(cm, ProcessIdentifier.DEFAULT_SCHEME))
                    .sender(ParticipantIdentifier.of(document.getSenderId()))
                    .receiver(ParticipantIdentifier.of(document.getRecipientId()))
                    .payLoad(inputStream);

            TransmissionRequest transmissionRequest = transmissionRequestBuilder.build();
            Transmitter transmitter = oxalisOutboundModule.getTransmitter();
            logger.info("Thread " + Thread.currentThread().getName() + " is about to send message: " + cm.toLog() + " to endpoint: " + transmissionRequest.getEndpoint());

            try {
                return transmitter.transmit(transmissionRequest);

            } catch (Exception e) {
                /**
                 * Temp workaround for process id protocol issue, need to find an exact way to determine correct protocol.
                 */
                String message = e.getMessage();
                if (StringUtils.isNotBlank(message) &&
                        message.replaceAll("\n", " ").replaceAll("\r", " ").contains("transport profile(s) not found")) {
                    transmissionRequest = transmissionRequestBuilder.processType(OxalisUtils.getPeppolProcessTypeId(cm, Scheme.of("sfti-procid"))).build();
                    return transmitter.transmit(transmissionRequest);
                } else {
                    throw e;
                }
            }
        }
    }
}