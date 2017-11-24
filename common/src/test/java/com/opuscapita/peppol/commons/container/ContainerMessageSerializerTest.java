package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.Route;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class ContainerMessageSerializerTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testBothDirections() throws Exception {
        ContainerMessage cm = new ContainerMessage("metadata", "fileName", Endpoint.TEST);

        DocumentInfo di = new DocumentInfo();
        di.setArchetype(Archetype.INVALID);
        di.setCustomizationId("customization_id");
        di.setDocumentId("document_id");
        di.setDocumentType("document_type");
        di.setDueDate("due_date");
        di.setIssueDate("issue_date");
        di.setProfileId("profile_id");
        di.setRecipientId("recipient_id");
        di.setSenderId("sender_id");
        cm.setDocumentInfo(di);

        Route route = new Route();
        route.setDescription("route_description");
        route.setEndpoints(Arrays.asList("endpoint_a", "endpoint_b"));
        route.setSource("test_source");

        ProcessingInfo pi = cm.getProcessingInfo();
        assertNotNull(pi);
        pi.setTransactionId("transaction_id");
        pi.setOriginalSource("original_source");
        pi.setRoute(route);

        ContainerMessageSerializer cms = new ContainerMessageSerializer();

        String message = cms.toJson(cm);

        ContainerMessage cm2 = cms.fromJson(message);

        assertEquals(Archetype.INVALID, cm2.getDocumentInfo().getArchetype());
        assertEquals("customization_id", cm2.getDocumentInfo().getCustomizationId());
        assertEquals("document_id", cm2.getDocumentInfo().getDocumentId());
        assertEquals("document_type", cm2.getDocumentInfo().getDocumentType());
        assertEquals("due_date", cm2.getDocumentInfo().getDueDate());
        assertEquals("issue_date", cm2.getDocumentInfo().getIssueDate());
        assertEquals("profile_id", cm2.getDocumentInfo().getProfileId());
        assertEquals("recipient_id", cm2.getDocumentInfo().getRecipientId());
        assertEquals("sender_id", cm2.getDocumentInfo().getSenderId());

        assertEquals("route_description", cm2.getProcessingInfo().getRoute().getDescription());
        assertEquals("test_source", cm2.getProcessingInfo().getRoute().getSource());
        assertEquals(2, cm2.getProcessingInfo().getRoute().getEndpoints().size());
        assertTrue(cm2.getProcessingInfo().getRoute().getEndpoints().contains("endpoint_a"));
        assertTrue(cm2.getProcessingInfo().getRoute().getEndpoints().contains("endpoint_b"));
        assertEquals("original_source", cm2.getProcessingInfo().getOriginalSource());

        assertEquals("transaction_id", cm2.getProcessingInfo().getTransactionId());
    }


}