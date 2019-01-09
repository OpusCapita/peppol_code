package com.opuscapita.peppol.events.persistence.amqp;

import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by bambr on 17.29.3.
 */
public class EventQueueListenerTest {

    private String fixture = "{\"transportType\":\"IN_IN\",\"fileName\":\"9908-997869699_errors.smtp\",\"fileSize\":980,\"invoiceId\":\"\",\"senderCountryCode\":\"\",\"recipientCountryCode\":\"\",\"invoiceDate\":\"\",\"dueDate\":\"\"}";

    @Test
    public void deserializePeppolEvent() throws Exception {
        EventQueueListener eventQueueListener = new EventQueueListener(new GsonBuilder().disableHtmlEscaping().create(), null, null);
        PeppolEvent event = eventQueueListener.deserializePeppolEvent(fixture);
        assertNotNull(event);
        assertNotNull(event.getProcessType());
        System.out.println(event.getProcessType());
    }
}