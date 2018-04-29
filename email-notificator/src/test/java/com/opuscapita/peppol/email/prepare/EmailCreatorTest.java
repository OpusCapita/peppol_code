package com.opuscapita.peppol.email.prepare;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.email.model.CombinedEmail;
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

        ContainerMessage cm = new ContainerMessage("metadata", "filename", Endpoint.TEST);
        cm.setDocumentInfo(new DocumentInfo());
        cm.getDocumentInfo().setRecipientId("customer_id");
        cm.getDocumentInfo().setSenderId("customer_id");

        String file = emailCreator.create("customer_id", cm, "Recipients", "Subject", "AAA", true);
        System.out.println(FileUtils.readFileToString(new File(file), Charset.defaultCharset()));

        CombinedEmail combinedEmail = new Gson().fromJson(FileUtils.readFileToString(new File(file), Charset.defaultCharset()), CombinedEmail.class);
        assertEquals("customer_id", combinedEmail.getCustomerId());
        assertEquals("Recipients", combinedEmail.getRecipients());
        assertEquals("Subject", combinedEmail.getCombinedSubject());
        assertEquals("AAA", combinedEmail.getCombinedBody());

        emailCreator.create("customer_id", cm, "Recipients2", "Subject2", "BBB", true);
        combinedEmail = new Gson().fromJson(FileUtils.readFileToString(new File(file), Charset.defaultCharset()), CombinedEmail.class);

        assertEquals("customer_id", combinedEmail.getCustomerId());
        assertEquals("Recipients", combinedEmail.getRecipients());
        assertTrue(combinedEmail.getCombinedSubject().equals("Subject; Subject2") || combinedEmail.getCombinedSubject().equals("Subject2; Subject"));
        assertTrue(combinedEmail.getCombinedBody().contains("AAA"));
        assertTrue(combinedEmail.getCombinedBody().contains("BBB"));

        System.out.println(combinedEmail.getTicket());

    }
}