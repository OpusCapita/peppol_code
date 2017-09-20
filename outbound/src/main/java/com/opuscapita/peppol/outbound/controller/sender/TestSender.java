package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import eu.peppol.BusDoxProtocol;
import eu.peppol.PeppolStandardBusinessHeader;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.identifier.TransmissionId;
import eu.peppol.outbound.transmission.*;
import eu.peppol.security.CommonName;
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
import java.net.URL;

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
    }

    @Override
    protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true);
    }

    @SuppressWarnings("unused")
    @Override
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        if (StringUtils.isBlank(testRecipient)) {
            logger.warn("Test sender selected but property 'peppol.outbound.test.recipient' is empty, using FakeSender instead");
            return fakeSender.send(cm);
        }

        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        logger.info("Sending message " + cm.getFileName() + " using test sender");
        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            TransmissionRequestBuilder requestBuilder = getTransmissionRequestBuilder();
            requestBuilder.reset();
            TransmissionRequestBuilder localRequestBuilder = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(PeppolProcessTypeId.valueOf(document.getProfileId()))
                    .sender(new ParticipantId(document.getSenderId()))
                    .receiver(new ParticipantId(testRecipient))
                    .trace(true)
                    .payLoad(getUpdatedFileContent(cm, testRecipient));

            TransmissionRequest transmissionRequest = requestBuilder.build();
            logger.debug("About to send " + cm.getFileName() + " using " + this.getClass().getSimpleName() + "and endpoint: "
                    + transmissionRequest.getEndpointAddress().getCommonName().toString());

            Transmitter transmitter = oxalisOutboundModule.getTransmitter();

            logger.info("Sending message " + cm.getFileName() + " to " + testRecipient);
            if (cm.getFileName().contains("-fail-me-io-")) {
                throw new IllegalStateException("This sending expected to fail I/O in test mode");
            }
            if (cm.getFileName().contains("-fail-me-")) {
                throw new IllegalStateException("This sending expected to fail in test mode");
            }

            if (cm.getFileName().contains("-integration-test-")) {
                TransmissionResponse fakeResult = new TransmissionResponse() {
                    @Override
                    public TransmissionId getTransmissionId() {
                        return new TransmissionId(cm.getFileName());
                    }

                    @Override
                    public PeppolStandardBusinessHeader getStandardBusinessHeader() {
                        return null;
                    }

                    @Override
                    public URL getURL() {
                        return null;
                    }

                    @Override
                    public BusDoxProtocol getProtocol() {
                        return null;
                    }

                    @Override
                    public CommonName getCommonName() {
                        return new CommonName("fake ap");
                    }

                    @Override
                    public byte[] getEvidenceBytes() {
                        return new byte[0];
                    }
                };
                return fakeResult;

            }

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

        String senderId = cm.getDocumentInfo().getSenderId();
        String recipientId = cm.getDocumentInfo().getRecipientId();

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
