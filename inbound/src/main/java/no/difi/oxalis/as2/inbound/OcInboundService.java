package no.difi.oxalis.as2.inbound;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.api.inbound.InboundService;
import no.difi.oxalis.api.statistics.StatisticsService;

/**
 * @author Sergejs.Roze
 */
@Singleton
public class OcInboundService implements InboundService {

    private StatisticsService statisticsService;

    @Inject
    public OcInboundService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public void complete(InboundMetadata inboundMetadata) {
        statisticsService.persist(inboundMetadata);
    }
}
