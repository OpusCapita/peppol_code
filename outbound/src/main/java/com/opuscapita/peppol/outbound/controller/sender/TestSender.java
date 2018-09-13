package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import no.difi.oxalis.api.lang.OxalisContentException;
import no.difi.oxalis.api.lang.OxalisTransmissionException;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.outbound.Transmitter;
import no.difi.oxalis.outbound.transmission.TransmissionRequestBuilder;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * More advanced test sender that the {@link FakeSender}.
 * It really sends data through the network but always uses the selected recipient instead of the real one.
 *
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
@Lazy
public class TestSender extends UblSender {
    private static final Logger logger = LoggerFactory.getLogger(TestSender.class);

    private final FakeSender fakeSender;

    @Value("${peppol.outbound.test.recipient:}")
    private String testRecipient;

    @Autowired
    public TestSender(@Nullable OxalisWrapper oxalisWrapper, @NotNull FakeSender fakeSender) {
        super(oxalisWrapper);
        this.fakeSender = fakeSender;
    }

    @PostConstruct
    @Autowired
    @Override
    public void initialize() {
        oxalisOutboundModule = oxalisWrapper.getOxalisOutboundModule();
    }

    @Override
    protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
        return oxalisWrapper.getTransmissionRequestBuilder(true);
    }

    @SuppressWarnings("unused")
    @Override
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException, OxalisContentException, OxalisTransmissionException, PeppolParsingException {
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

//            X509Certificate certificate = null;
//            try {
//                CertificateFactory fact = CertificateFactory.getInstance("X.509");
//                FileInputStream is = new FileInputStream("/home/rozeser1/.oxalis/some.crt");
//                certificate = (X509Certificate) fact.generateCertificate(is);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            TransmissionRequestBuilder requestBuilder = getTransmissionRequestBuilder();
            requestBuilder.reset();
            TransmissionRequestBuilder localRequestBuilder = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(ProcessIdentifier.of(document.getProfileId()))

//                    .overrideAs2Endpoint(Endpoint.of(
//                            TransportProfile.AS2_1_0,
//                            URI.create("http://localhost:8089/as2"),
//                            certificate))

                    .sender(ParticipantIdentifier.of(document.getSenderId()))
                    .receiver(ParticipantIdentifier.of(testRecipient))
                    .payLoad(getUpdatedFileContent(cm, testRecipient));

            TransmissionRequest transmissionRequest = requestBuilder.build();
            logger.info("About to send " + cm.getFileName() + " using " + this.getClass().getSimpleName() + "and endpoint: "
                    + transmissionRequest.getEndpoint());

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
                    public Endpoint getEndpoint() {
                        return null;
                    }

                    @Override
                    public TransmissionIdentifier getTransmissionIdentifier() {
                        return TransmissionIdentifier.of(cm.getFileName());
                    }

                    @Override
                    public Header getHeader() {
                        return null;
                    }

                    @Override
                    public Date getTimestamp() {
                        return null;
                    }

                    @Override
                    public Digest getDigest() {
                        return null;
                    }

                    @Override
                    public TransportProtocol getTransportProtocol() {
                        return null;
                    }

                    @Override
                    public List<Receipt> getReceipts() {
                        return null;
                    }

                    @Override
                    public Receipt primaryReceipt() {
                        return null;
                    }

                };
                logger.info("created fake TransmissionResponse for integration test with transmission id: " + fakeResult.getTransmissionIdentifier());
                return fakeResult;
            }

            TransmissionResponse result = transmitter.transmit(transmissionRequest);
            logger.info("Delivered message " + cm.getFileName() + " to URL " + result.getEndpoint() + " with transmission ID: " +
                    result.getTransmissionIdentifier());
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
