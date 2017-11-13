package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import eu.peppol.BusDoxProtocol;
import eu.peppol.PeppolStandardBusinessHeader;
import eu.peppol.identifier.TransmissionId;
import eu.peppol.outbound.transmission.TransmissionResponse;
import eu.peppol.security.CommonName;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    @Async("outbound-pool")
    @SuppressWarnings("unused")
    public CompletableFuture<TransmissionResponse> send(@NotNull ContainerMessage cm) throws IOException {
        if (cm.getFileName().contains("-fail-me-io-")) {
            logger.info("Rejecting message with I/O error as requested by the file name");
            throw new IOException("This sending expected to fail I/O in test mode");
        }
        if (cm.getFileName().contains("-fail-me-")) {
            logger.info("Rejecting message as requested by the file name");
            throw new IllegalStateException("This sending expected to fail in test mode");
        }

        // useful for multi-threading tests
//        try {
//            Thread.sleep(new Random().nextInt(9000));
//            logger.info("I'm in " + Thread.currentThread().getName());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        logger.info("Returning fake transmission result, to enable real sending set 'peppol.outbound.sending.enabled' to true");

        return CompletableFuture.completedFuture(new TransmissionResponse() {
            private final TransmissionId transmissionId = new TransmissionId(UUID.randomUUID());

            @Override
            public TransmissionId getTransmissionId() {
                return transmissionId;
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
                return null;
            }

            @Override
            public byte[] getEvidenceBytes() {
                return new byte[0];
            }
        });
    }
}
