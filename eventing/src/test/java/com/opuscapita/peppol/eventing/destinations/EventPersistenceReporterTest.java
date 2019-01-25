package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.Assert.assertNotNull;

/**
 * Created by bambr on 17.1.7.
 */
public class EventPersistenceReporterTest {

    private EventPersistenceReporter eventPersistenceReporter;
    private ContainerMessage containerMessage;

    @Before
    public void setUp() {
        eventPersistenceReporter = new EventPersistenceReporter(new RabbitTemplate(), new GsonBuilder().disableHtmlEscaping().create());
        Endpoint endpoint = new Endpoint("test", ProcessType.OUT_TEST);
        containerMessage = new ContainerMessage("/peppol/data/tmp/20170630/35163afa-8b49-416a-8766-1d2dd1a740ac.xml", endpoint);
        containerMessage.getProcessingInfo().setCurrentStatus(endpoint, "testing");
        containerMessage.getProcessingInfo().setPeppolMessageMetadata(PeppolMessageMetadata.createDummy());
    }

    @After
    public void tearDown() {
        eventPersistenceReporter = null;
        containerMessage = null;
    }

    @Test
    public void convert() {
        PeppolEvent event = eventPersistenceReporter.convert(containerMessage);
        assertNotNull(event);
        assertNotNull(event.getCommonName());
    }


}