package com.opuscapita.peppol.eventing.destinations;

import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
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
    String metadata = "{\n" +
            "  \"PeppolMessageMetaData\": {\n" +
            "    \"messageId\": \"ff3bb2dc-3ff4-11e6-9605-97ed9690fe22\",\n" +
            "    \"recipientId\": \"9908:937789416\",\n" +
            "    \"recipientSchemeId\": \"NO:ORGNR\",\n" +
            "    \"senderId\": \"9908:937270062\",\n" +
            "    \"senderSchemeId\": \"NO:ORGNR\",\n" +
            "    \"documentTypeIdentifier\": \"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1\",\n" +
            "    \"profileTypeIdentifier\": \"urn:www.cenbii.eu:profile:bii05:ver2.0\",\n" +
            "    \"sendingAccessPoint\": \"APP_1000000208\",\n" +
            "    \"receivingAccessPoint\": \"APP_1000000208\",\n" +
            "    \"protocol\": \"AS2\",\n" +
            "    \"userAgent\": null,\n" +
            "    \"userAgentVersion\": null,\n" +
            "    \"sendersTimeStamp\": null,\n" +
            "    \"receivedTimeStamp\": \"Fri Jun 30 09:08:14 UTC 2017\",\n" +
            "    \"transmissionId\": \"35163afa-8b49-416a-8766-1d2dd1a740ac\",\n" +
            "    \"buildUser\": \"redis\",\n" +
            "    \"buildDescription\": \"4.0.0-BETA1-24-g2bd3584\",\n" +
            "    \"buildTimeStamp\": \"24.05.2016 @ 14:31:27 EEST\",\n" +
            "    \"oxalis\": \"4.0.0-SNAPSHOT\"\n" +
            "  }\n" +
            "}";
    private EventPersistenceReporter eventPersistenceReporter;
    private ContainerMessage containerMessage;

    @Before
    public void setUp() {
        eventPersistenceReporter = new EventPersistenceReporter(new RabbitTemplate(), new GsonBuilder().disableHtmlEscaping().create());
        Endpoint endpoint = new Endpoint("test", ProcessType.OUT_TEST);
        containerMessage = new ContainerMessage(metadata, "/peppol/data/tmp/20170630/35163afa-8b49-416a-8766-1d2dd1a740ac.xml", endpoint);
        containerMessage.getProcessingInfo().setCurrentStatus(endpoint, "testing");
    }

    @After
    public void tearDown() {
        eventPersistenceReporter = null;
        containerMessage = null;
    }

    @Test
    public void convert() throws Exception {
        PeppolEvent event = eventPersistenceReporter.convert(containerMessage);
        assertNotNull(event);
        assertNotNull(event.getCommonName());
    }

}