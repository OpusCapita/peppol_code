package com.opuscapita.peppol.mlr.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class MessageLevelResponseCreatorTest {
    private OxalisErrorRecognizer oxalisErrorRecognizer = mock(OxalisErrorRecognizer.class);

    @Test
    public void reportSuccess() throws Exception {
        ContainerMessage cm = prepareContainerMessage();

        String result = new MessageLevelResponseCreator(oxalisErrorRecognizer).reportSuccess(cm);

        System.out.println(result);

        assertTrue(result.contains("<cbc:ID>doc_id-MLR</cbc:ID>"));
        assertTrue(result.contains(
                "<cac:SenderParty>" +
                        "<cbc:EndpointID>sender_id</cbc:EndpointID>" +
                        "<cac:PartyName>" +
                                "<cbc:Name>sender_name</cbc:Name>" +
                        "</cac:PartyName>" +
                "</cac:SenderParty>"
        ));
        assertTrue(result.contains(
                "<cac:ReceiverParty>" +
                        "<cbc:EndpointID>recipient_id</cbc:EndpointID>" +
                        "<cac:PartyName>" +
                                "<cbc:Name>recipient_name&lt;&gt;</cbc:Name>" +
                        "</cac:PartyName>" +
                "</cac:ReceiverParty>"
        ));
        assertTrue(result.contains(
                "<cac:DocumentResponse>" +
                        "<cac:Response>" +
                                "<cbc:ResponseCode>AP</cbc:ResponseCode>" +
                        "</cac:Response>" +
                        "<cac:DocumentReference>" +
                                "<cbc:ID>doc_id</cbc:ID>" +
                        "</cac:DocumentReference>" +
                "</cac:DocumentResponse>"
        ));
        assertFalse(result.contains("#"));
        assertFalse(result.contains("$"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void testBugWithUnparsableIssueDate() throws Exception {
        ContainerMessage cm = prepareContainerMessage();

        cm.getDocumentInfo().setIssueDate("oops");

        String result = new MessageLevelResponseCreator(oxalisErrorRecognizer).reportSuccess(cm);

        System.out.println(result);
        assertTrue(result.contains("<cbc:ResponseCode>RE</cbc:ResponseCode>"));
        assertTrue(result.contains("<cbc:StatusReasonCode>SV</cbc:StatusReasonCode>"));
        assertTrue(result.contains("DOCUMENT_ERROR"));
        assertFalse(result.contains("#"));
        assertFalse(result.contains("$"));
    }

    private ContainerMessage prepareContainerMessage() {
        ContainerMessage cm = new ContainerMessage("meatdata", "test.xml", Endpoint.TEST);

        DocumentInfo di = new DocumentInfo();
        di.setDocumentId("doc_id");
        di.setIssueDate("2017-07-18");
        di.setIssueTime("11:12:13");
        di.setRecipientId("recipient_id");
        di.setRecipientName("recipient_name<>");
        di.setSenderId("sender_id");
        di.setSenderName("sender_name");
        cm.setDocumentInfo(di);

        cm.setStatus(Endpoint.TEST, "testing");

        return cm;
    }

}
