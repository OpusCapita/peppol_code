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
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
public class UblSender implements PeppolSender {
    private final static Logger logger = LoggerFactory.getLogger(UblSender.class);

    final OxalisWrapper oxalisWrapper;

    OxalisOutboundComponent oxalisOutboundModule;

    @Autowired
    public UblSender(OxalisWrapper oxalisWrapper) {
        this.oxalisWrapper = oxalisWrapper;
    }

    @PostConstruct
    public void initialize() {
        oxalisOutboundModule = oxalisWrapper.getOxalisOutboundModule();
    }

    protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisWrapper.getTransmissionRequestBuilder(false);
    }

    @SuppressWarnings("unused")
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException, OxalisContentException, OxalisTransmissionException, PeppolParsingException {
        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        // request builder is not reusable
        TransmissionRequestBuilder requestBuilder = getTransmissionRequestBuilder();

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequestBuilder localRequestBuilder = requestBuilder
                    .payLoad(inputStream);

            TransmissionRequest transmissionRequest = requestBuilder.build();
            logger.info("Thread " + Thread.currentThread().getName() + " is about to send " + cm.getFileName() + ", endpoint: " +
                    transmissionRequest.getEndpoint());

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            return transmitter.transmit(transmissionRequest);
        }
    }

}
