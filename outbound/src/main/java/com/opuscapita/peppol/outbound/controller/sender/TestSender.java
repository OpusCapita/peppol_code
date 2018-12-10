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

    @SuppressWarnings("unused")
    @Override
    @NotNull
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException, OxalisContentException, OxalisTransmissionException {
        if (StringUtils.isBlank(testRecipient)) {
            logger.warn("Test sender selected but property 'peppol.outbound.test.recipient' is empty, using FakeSender instead");
            return fakeSender.send(cm);
        }

        DocumentInfo document = cm.getDocumentInfo();
        if (document == null) {
            throw new IllegalArgumentException("There is no document in message");
        }

        TransmissionRequestBuilder requestBuilder = getTransmissionRequestBuilder();

        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {

//            X509Certificate certificate = null;
//            try {
//                CertificateFactory fact = CertificateFactory.getInstance("X.509");
//                FileInputStream is = new FileInputStream("C:\\Users\\ibilge\\.oxalis\\oxalis.cer");
//                certificate = (X509Certificate) fact.generateCertificate(is);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            TransmissionRequest transmissionRequest = requestBuilder
                    .documentType(OxalisUtils.getPeppolDocumentTypeId(document))
                    .processType(ProcessIdentifier.of(document.getProfileId()))

//                    .overrideAs2Endpoint(Endpoint.of(
//                            TransportProfile.AS2_1_0,
//                            URI.create("http://localhost:8089/as2"),
//                            certificate))

                    .sender(ParticipantIdentifier.of(document.getSenderId()))
                    .receiver(ParticipantIdentifier.of(testRecipient))
                    .payLoad(getUpdatedFileContent(cm, testRecipient))
                    .build();

            logger.info("About to send " + cm.getFileName() + " to receiver: " + testRecipient + " using " + this.getClass().getSimpleName() + " to endpoint: " + transmissionRequest.getEndpoint());

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

            Transmitter transmitter = getOxalisOutboundModule().getTransmitter();
            return transmitter.transmit(transmissionRequest);
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
