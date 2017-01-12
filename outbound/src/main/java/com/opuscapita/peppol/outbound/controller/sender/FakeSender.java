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
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.UUID;

/**
 * Faked sender for testing without sending real data. Does nothing, just returns fake TransmissionResponse.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class FakeSender implements PeppolSender {
    private static final Logger logger = LoggerFactory.getLogger(FakeSender.class);

    @SuppressWarnings("unused")
    public TransmissionResponse send(@NotNull ContainerMessage cm) {
        logger.info("Returning fake transmission result, to enable real sending set 'peppol.outbound.sending.enabled' to true");

        return new TransmissionResponse() {
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
        };
    }
}
