package com.opuscapita.peppol.inbound.module;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.settings.Settings;
import no.difi.oxalis.api.util.Type;
import no.difi.oxalis.as2.api.MessageIdGenerator;
import no.difi.oxalis.as2.common.As2Conf;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Singleton
@Type("opuscapita")
public class OcMessageIdGenerator implements MessageIdGenerator {

    private String hostname;

    @Inject
    public OcMessageIdGenerator(Settings<As2Conf> settings) {
        try {
            hostname = settings.getString(As2Conf.HOSTNAME);
            if (hostname.trim().isEmpty())
                hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Unable to get local hostname.", e);
        }
    }

    public OcMessageIdGenerator(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String generate(TransmissionRequest transmissionRequest) {
        return transmissionRequest.getHeader().getIdentifier().getIdentifier();
    }

    @Override
    public String generate(InboundMetadata inboundMetadata) {
        return inboundMetadata.getHeader().getIdentifier().getIdentifier();
    }
}
