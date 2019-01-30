package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import no.difi.oxalis.api.model.TransmissionIdentifier;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.vefa.peppol.common.model.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import sun.security.x509.X509CertImpl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@Scope("prototype")
@Lazy
public class FakeSender implements PeppolSender {

    private static final Logger logger = LoggerFactory.getLogger(FakeSender.class);

    /**
     * Throws specific type of exception when filename includes specific keyword for testing purposes
     */
    private boolean throwExceptionIfExpectedInFilename(@NotNull ContainerMessage cm) throws IOException {
        if (cm.getFileName().contains("-fail-me-unknown-recipient-")) {
            logger.info("Rejecting message with LookupException as requested by the file name");
            throw new RuntimeException("This sending expected to fail with LookupException: Identifier 9908:919779446 is not registered in SML test mode");
        }
        if (cm.getFileName().contains("-fail-me-unsupported-data-format-")) {
            logger.info("Rejecting message with LookupException as requested by the file name");
            throw new RuntimeException("This sending expected to fail with LookupException: Combination of receiver 9908:919779446 and document type identifier peppol-bis-v3 is not supported test mode");
        }
        if (cm.getFileName().contains("-fail-me-receiving-ap-error-")) {
            logger.info("Rejecting message as requested by the file name");
            throw new RuntimeException("This sending expected to fail with exception: Receiving server does not seem to be running test mode");
        }
        if (cm.getFileName().contains("-fail-me-io-")) {
            logger.info("Rejecting message with I/O error as requested by the file name");
            throw new IOException("This sending expected to fail I/O in test mode");
        }
        if (cm.getFileName().contains("-fail-me-")) {
            logger.info("Rejecting message as requested by the file name");
            throw new IllegalStateException("This sending expected to fail in test mode");
        }
        return true;
    }

    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        logger.info("Thread " + Thread.currentThread().getName() + " about to emulate sending " + cm.getFileName() + " using " + this.getClass().getSimpleName());
        throwExceptionIfExpectedInFilename(cm);
        logger.info("Returning fake transmission result, to enable real sending set 'peppol.outbound.sending.enabled' to true");

        return new TransmissionResponse() {

            @Override
            public TransmissionIdentifier getTransmissionIdentifier() {
                return TransmissionIdentifier.generateUUID();
            }

            @Override
            public Header getHeader() {
                Header header = Header.newInstance();
                header.identifier(InstanceIdentifier.generateUUID());
                header.sender(ParticipantIdentifier.of("test"));
                header.receiver(ParticipantIdentifier.of("test"));
                header.process(ProcessIdentifier.NO_PROCESS);
                header.documentType(DocumentTypeIdentifier.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1"));
                header.instanceType(InstanceType.of("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2", "Invoice", "2.1"));
                return header;
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

            @Override
            public Endpoint getEndpoint() {
                return Endpoint.of(TransportProfile.AS2_1_0, null, new X509CertImpl());
            }
        };
    }
}
