package com.opuscapita.peppol.commons.model.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by bambr on 16.5.9.
 */
public class PeppolEventDeSerializerTest {
    private String[] fixtures = {
            "{}",
            "{\"transportType\":\"OUT_IN\",\"fileName\":\"ubl-40071810-6def-11e6-aad8-8f9310c3c4b8.xml\",\"fileSize\":0,\"invoiceId\":\"\",\"senderCountryCode\":\"\",\"recipientCountryCode\":\"\",\"invoiceDate\":\"\",\"dueDate\":\"\"}",
    };
    private com.opuscapita.peppol.commons.model.util.PeppolEventDeSerializer testObject;

    @Before
    public void setUp() throws Exception {
        testObject = new com.opuscapita.peppol.commons.model.util.PeppolEventDeSerializer();
    }

    @After
    public void tearDown() throws Exception {
        testObject = null;
    }

    @Test
    public void testPopulatePEppolEvent() throws Exception {
        Arrays.asList(fixtures).forEach(this::testPopulateEventWithFixture);
    }

    private void testPopulateEventWithFixture(String fixture) {
        PeppolEvent testPeppolEvent = testObject.fixSenderName(new Gson().fromJson(fixture, JsonElement.class).getAsJsonObject());
        assertNotNull(testPeppolEvent.getSenderName());
        assertEquals("n/a", testPeppolEvent.getSenderName());
    }

}
