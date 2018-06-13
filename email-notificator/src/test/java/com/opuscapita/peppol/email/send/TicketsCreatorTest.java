package com.opuscapita.peppol.email.send;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.model.Recipient;
import com.opuscapita.peppol.email.model.SingleEmail;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TicketsCreatorTest {

    @Test
    public void createTickets() {
        ArgumentCaptor<String> shortDescription = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> longDescription = ArgumentCaptor.forClass(String.class);
        ErrorHandler errorHandler = mock(ErrorHandler.class);

        Recipient recipient = new Recipient(Recipient.Type.AP, "recipient_id", "recipient_name", "email_address");

        CombinedEmail combinedEmail = new CombinedEmail(true, recipient);

        ContainerMessage cm1 = new ContainerMessage("meatdata", "file_name_1", Endpoint.TEST);
        ContainerMessage cm2 = new ContainerMessage("meatdata", "file_name_2", Endpoint.TEST);

        SingleEmail se1 = new SingleEmail(cm1, "subject_1", "body_1");
        SingleEmail se2 = new SingleEmail(cm2, "subject_2", "body_2");

        combinedEmail.addMail(se1);
        combinedEmail.addMail(se2);

        TicketsCreator ticketsCreator = new TicketsCreator(errorHandler);

        ticketsCreator.createTicket(combinedEmail, "combined_file_name");

        verify(errorHandler).reportWithoutContainerMessage(any(), any(), shortDescription.capture(), any(), any(), longDescription.capture());
        System.out.println(shortDescription.getValue());
        System.out.println(longDescription.getValue());

        assertTrue(shortDescription.getValue().contains("recipient_id"));
        assertTrue(shortDescription.getValue().contains("recipient_name"));
        assertTrue(longDescription.getValue().contains("subject_1"));
        assertTrue(longDescription.getValue().contains("subject_2"));
        assertTrue(longDescription.getValue().contains("body_1"));
        assertTrue(longDescription.getValue().contains("body_2"));
        assertTrue(longDescription.getValue().contains("email_address"));
        assertTrue(longDescription.getValue().contains("file_name_1"));
        assertTrue(longDescription.getValue().contains("file_name_2"));

    }
}