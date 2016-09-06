package com.opuscapita.peppol.events.persistence.model.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.opuscapita.peppol.events.persistence.model.PeppolEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by bambr on 16.5.9.
 */
public class PeppolEventDeSerializerTest {
    String[] fixtures = {
            "{}",
            "{\"transportType\":\"OUT_IN\",\"fileName\":\"ubl-40071810-6def-11e6-aad8-8f9310c3c4b8.xml\",\"fileSize\":0,\"invoiceId\":\"\",\"senderCountryCode\":\"\",\"recipientCountryCode\":\"\",\"invoiceDate\":\"\",\"dueDate\":\"\"}",
            "{\"transportType\":\"IN_OUT\",\"fileName\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8.xml\",\"fileSize\":38497,\"invoiceId\":\"356042\",\"senderId\":\"9908:919095105\",\"senderName\":\"LAKS-- VILDTCENTRALEN AS\",\"senderCountryCode\":\"NO\",\"recipientId\":\"9908:994356550\",\"recipientName\":\"NIWA\",\"recipientCountryCode\":\"NO\",\"invoiceDate\":\"2016-05-31\",\"dueDate\":\"2016-06-21\",\"transactionId\":\"7ecd16db-bfc1-4374-9be7-3ec00a56e9f8\",\"documentType\":\"Invoice\",\"commonName\":\"O\\u003dEVRY Norge AS,CN\\u003dAPP_1000000148,C\\u003dNO\",\"sendingProtocol\":\"AS2\"}"
    };
    private PeppolEventDeSerializer testObject;

    @Before
    public void setUp() throws Exception {
        testObject = new PeppolEventDeSerializer();
    }

    @After
    public void tearDown() throws Exception {
        testObject = null;
    }

    @Test
    public void testPopulatePEppolEvent() throws Exception {
        Arrays.asList(fixtures).forEach(fixture -> {
            testPopulateEventWithFixture(fixture);
        });
    }

    protected void testPopulateEventWithFixture(String fixture) {
        PeppolEvent testPeppolEvent = new PeppolEvent();
        testObject.populatePeppolEvent(new Gson().fromJson(fixture, JsonElement.class), testPeppolEvent);
        System.out.println(testPeppolEvent);
        assertEquals("n/a", testPeppolEvent.getSenderName());
    }

}