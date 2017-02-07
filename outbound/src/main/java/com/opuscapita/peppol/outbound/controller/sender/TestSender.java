package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.transmission.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;

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

    @Value("${peppol.outbound.test.recipient:}")
    private String testRecipient;

    @Autowired
    public TestSender(@Nullable OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper, @NotNull FakeSender fakeSender) {
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
                    .payLoad(getUpdatedFileContent(cm, testRecipient));

            TransmissionRequest transmissionRequest = requestBuilder.build();

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            logger.info("Sending message " + cm.getFileName() + " to " + testRecipient);
            TransmissionResponse result = transmitter.transmit(transmissionRequest);
            logger.info("Delivered message " + cm.getFileName() + " to URL " + result.getURL() + " with transmission ID: " +
                    result.getTransmissionId());
            return result;
        }
    }

    @SuppressWarnings("ConstantConditions")
    InputStream getUpdatedFileContent(@NotNull ContainerMessage cm, @NotNull String fakeId) throws IOException {
        long sizeL = new File(cm.getFileName()).length();
        int sizeI = sizeL > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sizeL;
        org.apache.commons.io.output.ByteArrayOutputStream result = new org.apache.commons.io.output.ByteArrayOutputStream(sizeI);

        String senderId = cm.getBaseDocument().getSenderId();
        String recipientId = cm.getBaseDocument().getRecipientId();

        try (BufferedReader reader = new BufferedReader(new FileReader(cm.getFileName()))) {
            String line = reader.readLine();
            while (line != null) {
                line = StringUtils.replace(line, senderId, fakeId);
                line = StringUtils.replace(line, recipientId, fakeId);
                result.write(line.getBytes());
                result.write("\n".getBytes());
                line = reader.readLine();
            }
        }

        return new ByteArrayInputStream(result.toByteArray());
    }
}
