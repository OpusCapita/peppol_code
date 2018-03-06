package com.opuscapita.peppol.transport.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.events.Message;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class TransportControllerTest {
    TransportController transportController;

    @Before
    public void setUp() throws Exception {
        transportController = new TransportController(null, null, new Gson());
    }

    @After
    public void tearDown() throws Exception {
        transportController = null;
    }

    @Test
    public void createJsonEncodedPayloadObject() throws IOException, URISyntaxException {

        URI resource = TransportController.class.getResource("/valid/ehf.xml").toURI();
        File resourceFile = new File(resource);
        String metadata = "metadata";
        String fileName = resourceFile.getAbsolutePath();
        Endpoint source = new Endpoint("unit test", ProcessType.TEST);
        ContainerMessage containerMessage = new ContainerMessage(metadata, fileName, source);
        containerMessage.getProcessingInfo().setEventingMessage(new Message("id", System.currentTimeMillis(), true, null));
        String jsonEncodedPayloadObject = transportController.createJsonEncodedPayloadObject(containerMessage);
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonEncodedPayloadObject, JsonObject.class);
        assertTrue(jsonObject.has("payload"));
        assertTrue(jsonObject.has("metadata"));
        assertTrue(jsonObject.get("metadata").getAsJsonObject().has("created"));
        assertTrue(jsonObject.get("metadata").getAsJsonObject().has("filename"));
    }
}