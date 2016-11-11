package com.opuscapita.peppol.outbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import eu.peppol.BusDoxProtocol;
import eu.peppol.PeppolStandardBusinessHeader;
import eu.peppol.identifier.TransmissionId;
import eu.peppol.outbound.transmission.TransmissionResponse;
import eu.peppol.security.CommonName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.UUID;

/**
 * Faked sender for testing without sending real data.
 *
 * @author Sergejs.Roze
 */
@Component
public class TestSender {
    private static final Logger logger = LoggerFactory.getLogger(TestSender.class);

    TransmissionResponse send(ContainerMessage cm) {
        logger.info("Returning fake transmission result, to enable real sending set 'peppol.outbound.sending.enabled' to true");

        return new TransmissionResponse() {
            @Override
            public TransmissionId getTransmissionId() {
                return new TransmissionId(UUID.randomUUID());
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
