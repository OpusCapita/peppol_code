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

/**
 * Faked sender for testing without sending real data. Does nothing, just returns fake TransmissionResponse.
 *
 * @author Sergejs.Roze
 */
@Component
@Scope("prototype")
@Lazy
public class FakeSender implements PeppolSender {
    private static final Logger logger = LoggerFactory.getLogger(FakeSender.class);

    @SuppressWarnings("unused")
    public TransmissionResponse send(@NotNull ContainerMessage cm) throws IOException {
        logger.info("Thread " + Thread.currentThread().getName() + " about to emulate sending " + cm.getFileName() + " using " + this.getClass().getSimpleName());

        if (cm.getFileName().contains("-fail-me-io-")) {
            logger.info("Rejecting message with I/O error as requested by the file name");
            throw new IOException("This sending expected to fail I/O in test mode");
        }
        if (cm.getFileName().contains("-fail-me-")) {
            logger.info("Rejecting message as requested by the file name");
            throw new IllegalStateException("This sending expected to fail in test mode");
        }

        logger.info("Returning fake transmission result, to enable real sending set 'peppol.outbound.sending.enabled' to true");

        return new TransmissionResponse() {
            @Override
            public TransmissionIdentifier getTransmissionIdentifier() {
                return TransmissionIdentifier.generateUUID();
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

            @Override
            public Endpoint getEndpoint() {
                return Endpoint.of(TransportProfile.AS2_1_0, null, new X509CertImpl());
            }
        };
    }
}
