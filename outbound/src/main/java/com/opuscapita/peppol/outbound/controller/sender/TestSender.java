package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.transmission.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * More advanced test sender that the {@link FakeSender}.
 * It really sends data through the network but always uses the selected recipient instead of the real one.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class TestSender extends UblSender {
    private static final Logger logger = LoggerFactory.getLogger(TestSender.class);

    private final FakeSender fakeSender;

    @Value("${peppol.outbound.test.recipient:''}")
    private String testRecipient;

    @Autowired
    public TestSender(OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper, FakeSender fakeSender) {
        super(oxalisOutboundModuleWrapper);
        this.fakeSender = fakeSender;
    }

    @PostConstruct
    @Autowired
    @Override
    public void initialize() {
        oxalisOutboundModule = oxalisOutboundModuleWrapper.getOxalisOutboundModule();
        requestBuilder = oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true);
    }

    @SuppressWarnings("unused")
    @Override
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        if (StringUtils.isBlank(testRecipient)) {
            logger.warn("Test sender selected but property 'peppol.outbound.test.recipient' is empty, using FakeSender instead");
            return fakeSender.send(cm);
        }

        BaseDocument document = cm.getBaseDocument();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequestBuilder localRequestBuilder = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(PeppolProcessTypeId.valueOf(document.getProfileId()))
                    .sender(new ParticipantId(document.getSenderId()))
                    .receiver(new ParticipantId(testRecipient))
                    .trace(true)
                    .payLoad(inputStream);

            TransmissionRequest transmissionRequest = requestBuilder.build();

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            return transmitter.transmit(transmissionRequest);
        }

    }
}
