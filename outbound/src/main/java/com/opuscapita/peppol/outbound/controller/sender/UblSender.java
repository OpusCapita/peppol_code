package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.*;
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

    final OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper;

    OxalisOutboundModule oxalisOutboundModule;

    @Autowired
    public UblSender(OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper) {
        this.oxalisOutboundModuleWrapper = oxalisOutboundModuleWrapper;
    }

    @PostConstruct
    public void initialize() {
        oxalisOutboundModule = oxalisOutboundModuleWrapper.getOxalisOutboundModule();
    }

    protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(false);
    }

    @SuppressWarnings("unused")
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        // request builder is not reusable
        TransmissionRequestBuilder requestBuilder = getTransmissionRequestBuilder();

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequestBuilder localRequestBuilder = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(PeppolProcessTypeId.valueOf(document.getProfileId()))
                    .sender(new ParticipantId(document.getSenderId()))
                    .receiver(new ParticipantId(document.getRecipientId()))
                    .trace(true)
                    .payLoad(inputStream);

            TransmissionRequest transmissionRequest = requestBuilder.build();
            logger.info("Thread " + Thread.currentThread().getName() + " is about to send " + cm.getFileName() + ", endpoint: " +
                    transmissionRequest.getEndpointAddress().getCommonName().toString());

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            return transmitter.transmit(transmissionRequest);
        }
    }

}
