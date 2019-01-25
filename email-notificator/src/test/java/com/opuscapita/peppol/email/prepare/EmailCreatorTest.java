package com.opuscapita.peppol.email.prepare;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.model.Recipient;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ConstantConditions")
public class EmailCreatorTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void create() throws IOException {
        File temp = folder.newFolder();

        ErrorHandler errorHandler = mock(ErrorHandler.class);
        EmailCreator emailCreator = new EmailCreator(errorHandler, new Gson());
        emailCreator.setDirectory(temp.getAbsolutePath());

        ContainerMessage cm = new ContainerMessage("filename", Endpoint.TEST);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setRecipientId("customer_id");
        cm.getDocumentInfo().setSenderId("customer_id");

        Recipient recipient = new Recipient(Recipient.Type.CUSTOMER, "recipient_id", "recipient_name", "email_addresses");

        String file = emailCreator.create(recipient, cm, "subject", "body_1", true);
        System.out.println(FileUtils.readFileToString(new File(file), Charset.defaultCharset()));

        CombinedEmail combinedEmail = new Gson().fromJson(FileUtils.readFileToString(new File(file), Charset.defaultCharset()), CombinedEmail.class);
        assertEquals("recipient_id", combinedEmail.getRecipient().getId());
        assertEquals("email_addresses", combinedEmail.getRecipient().getAddresses());
        assertEquals("subject", combinedEmail.getCombinedSubject());
        assertEquals("body_1", combinedEmail.getCombinedBody());

        emailCreator.create(recipient, cm, "subject_2", "body_2", true);
        combinedEmail = new Gson().fromJson(FileUtils.readFileToString(new File(file), Charset.defaultCharset()), CombinedEmail.class);

        assertEquals("recipient_id", combinedEmail.getRecipient().getId());
        assertEquals("email_addresses", combinedEmail.getRecipient().getAddresses());
        assertTrue(combinedEmail.getCombinedSubject().equals("subject; subject_2") || combinedEmail.getCombinedSubject().equals("subject_2; subject"));
        assertTrue(combinedEmail.getCombinedBody().contains("body_1"));
        assertTrue(combinedEmail.getCombinedBody().contains("body_2"));

        System.out.println(combinedEmail.getTicketHeader());
        System.out.println(combinedEmail.getTicketBody());

    }
}