package com.opuscapita.peppol.eventing.destinations.mlr;

import com.helger.ubl21.UBL21Writer;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class MessageLevelResponseCreatorTest {

    @Test
    public void reportSuccess() throws Exception {
        ContainerMessage cm = new ContainerMessage("meatdata", "test.xml", new Endpoint("xxx", ProcessType.TEST));

        DocumentInfo di = new DocumentInfo();
        di.setDocumentId("doc_id");
        di.setIssueDate("2017-07-18");
        di.setIssueTime("11:12:13");
        di.setRecipientId("recipient_id");
        di.setRecipientName("recipient_name");
        di.setSenderId("sender_id");
        di.setSenderName("sender_name");
        cm.setDocumentInfo(di);

        ApplicationResponseType art = new MessageLevelResponseCreator().reportSuccess(cm);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        UBL21Writer.applicationResponse().write(art, out);

        String result = out.toString();
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
                                "<cbc:Name>recipient_name</cbc:Name>" +
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
    }

}