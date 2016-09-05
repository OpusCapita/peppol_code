package com.opuscapita.peppol.events.persistence.model.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.opuscapita.peppol.events.persistence.model.PeppolEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bambr on 16.5.9.
 */
public class PeppolEventDeSerializerTest {
    private PeppolEventDeSerializer testObject;
    String fixutre = "{}";

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
        PeppolEvent testPeppolEvent = new PeppolEvent();
        testObject.populatePeppolEvent(new Gson().fromJson(fixutre, JsonElement.class), testPeppolEvent);
        assertEquals("n/a", testPeppolEvent.getSenderName());
    }

}