package no.difi.oxalis.statistics.service;

import brave.Span;
import brave.Tracer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.oxalis.api.model.AccessPointIdentifier;
import no.difi.oxalis.api.util.Type;
import no.difi.oxalis.statistics.api.ChannelId;
import no.difi.oxalis.statistics.model.DefaultRawStatistics;
import no.difi.oxalis.api.model.Direction;
import no.difi.oxalis.statistics.api.RawStatisticsRepository;
import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.outbound.TransmissionRequest;
import no.difi.oxalis.api.outbound.TransmissionResponse;
import no.difi.oxalis.api.statistics.StatisticsService;
import no.difi.oxalis.commons.security.CertificateUtils;
import no.difi.oxalis.commons.tracing.Traceable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;

@Singleton
@Type("default")
/* Copied from oxalis */
public class DefaultStatisticsService extends Traceable implements StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStatisticsService.class);

    private final RawStatisticsRepository rawStatisticsRepository;

    private final AccessPointIdentifier ourAccessPointIdentifier;

    @Inject
    public DefaultStatisticsService(RawStatisticsRepository rawStatisticsRepository,
                                    X509Certificate certificate, Tracer tracer) {
        super(tracer);
        this.rawStatisticsRepository = rawStatisticsRepository;
        this.ourAccessPointIdentifier = new AccessPointIdentifier(CertificateUtils.extractCommonName(certificate));
    }

    @Override
    public void persist(TransmissionRequest transmissionRequest, TransmissionResponse transmissionResponse, Span root) {
        Span span = tracer.newChild(root.context()).name("persist statistics").start();
        try {
            DefaultRawStatistics.RawStatisticsBuilder builder = new DefaultRawStatistics.RawStatisticsBuilder()
                    .accessPointIdentifier(ourAccessPointIdentifier)
                    .direction(Direction.OUT)
                    .documentType(transmissionResponse.getHeader().getDocumentType())
                    .sender(transmissionResponse.getHeader().getSender())
                    .receiver(transmissionResponse.getHeader().getReceiver())
                    .profile(transmissionResponse.getHeader().getProcess())
                    .date(transmissionResponse.getTimestamp());  // Time stamp of reception of the receipt

            // If we know the CN name of the destination AP, supply that
            // as the channel id otherwise use the protocol name
            if (transmissionRequest.getEndpoint().getCertificate() != null) {
                String accessPointIdentifierValue = CertificateUtils
                        .extractCommonName(transmissionRequest.getEndpoint().getCertificate());
                builder.channel(new ChannelId(accessPointIdentifierValue));
            } else {
                String protocolName = transmissionRequest.getEndpoint().getTransportProfile().getIdentifier();
                builder.channel(new ChannelId(protocolName));
            }

            DefaultRawStatistics rawStatistics = builder.build();
            rawStatisticsRepository.persist(rawStatistics);
        } catch (Exception ex) {
            span.tag("exception", String.valueOf(ex.getMessage()));
            logger.error("Persisting DefaultRawStatistics about oubound transmission failed : {}", ex.getMessage(), ex);
        } finally {
            span.finish();
        }
    }

    public void persist(InboundMetadata inboundMetadata) {
        // Persists raw statistics when message was received (ignore if stats couldn't be persisted, just warn)
        try {
            DefaultRawStatistics rawStatistics = new DefaultRawStatistics.RawStatisticsBuilder()
                    .accessPointIdentifier(ourAccessPointIdentifier)
                    .direction(Direction.IN)
                    .documentType(inboundMetadata.getHeader().getDocumentType())
                    .sender(inboundMetadata.getHeader().getSender())
                    .receiver(inboundMetadata.getHeader().getReceiver())
                    .profile(inboundMetadata.getHeader().getProcess())
                    .channel(new ChannelId("AS2"))
                    .build();

            rawStatisticsRepository.persist(rawStatistics);
        } catch (Exception e) {
            logger.error("Unable to persist statistics for " + inboundMetadata.toString() + ";\n " + e.getMessage(), e);
        }
    }
}
