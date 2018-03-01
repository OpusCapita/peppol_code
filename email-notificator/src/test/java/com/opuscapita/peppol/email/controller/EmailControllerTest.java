package com.opuscapita.peppol.email.controller;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.email.model.CustomerRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Gamans
 */
public class EmailControllerTest {
    private static final String INVALID_ERROR_MESSAGE = "INVALID error message";
    private static final String TRANSPORT_ERROR_MESSAGE = "Problem with SMP lookup for participant TEST and document type TEST";

    private EmailController controller;
    private CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
    private ErrorHandler errorHandler = Mockito.mock(ErrorHandler.class);
    private BodyFormatter bodyFormatter = new BodyFormatter();
    private OxalisErrorRecognizer oxalisErrorRecognizer = Mockito.mock(OxalisErrorRecognizer.class);
    private File directory;
    private Customer customer = new Customer();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void before() {
        directory = temporaryFolder.getRoot();

        customer.setInboundEmails("inbound");
        customer.setOutboundEmails("outbound");
        when(customerRepository.findByIdentifier(any())).thenReturn(customer);

        controller = new EmailController(customerRepository, errorHandler, bodyFormatter, oxalisErrorRecognizer);
        controller.setDirectory(directory.getAbsolutePath());
        controller.setOutInvalidEmailSubject("OUT_INVALID_EMAIL_SUBJECT");
        controller.setOutLookupErrorEmailSubject("INVALID_LOOKUP_SUBJECT");
        controller.setInInvalidEmailSubject("IN_INVALID_EMAIL_SUBJECT");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void processDocumentOutLookupError() throws Exception {
        ContainerMessage cm = createTestContainerMessage();
        cm.getProcessingInfo().setProcessingException(TRANSPORT_ERROR_MESSAGE);

        when(oxalisErrorRecognizer.recognize(TRANSPORT_ERROR_MESSAGE)).thenReturn(SendingErrors.UNKNOWN_RECIPIENT);

        controller.processMessage(cm);

        File subjectFile = new File(directory, "customer_id" + EmailController.EXT_SUBJECT);
        File toFile = new File(directory, "customer_id" + EmailController.EXT_TO);
        File bodyFile = new File(directory, "customer_id" + EmailController.EXT_BODY);

        //files created
        Assert.assertTrue(subjectFile.exists());
        Assert.assertTrue(toFile.exists());
        Assert.assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains("INVALID_LOOKUP_SUBJECT"));
        content = Files.toString(toFile, Charsets.UTF_8);
        Assert.assertEquals(content, "outbound"); //should be the one used for outbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains(TRANSPORT_ERROR_MESSAGE));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void processDocumentInValidationError() throws Exception {
        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.IN_IN), INVALID_ERROR_MESSAGE));
        cm.setProcessingInfo(new ProcessingInfo(new Endpoint("test", ProcessType.IN_IN), "metadata"));

        when(oxalisErrorRecognizer.recognize(INVALID_ERROR_MESSAGE)).thenReturn(SendingErrors.DATA_ERROR);

        controller.processMessage(cm);

        File subjectFile = new File(directory, "recipient_id" + EmailController.EXT_SUBJECT);
        File toFile = new File(directory, "recipient_id" + EmailController.EXT_TO);
        File bodyFile = new File(directory, "recipient_id" + EmailController.EXT_BODY);

        //files created
        Assert.assertTrue(subjectFile.exists());
        Assert.assertTrue(toFile.exists());
        Assert.assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains("IN_INVALID_EMAIL_SUBJECT"));
        content = Files.toString(toFile, Charsets.UTF_8);
        Assert.assertEquals(content, "inbound"); //should be the one used for inbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains(INVALID_ERROR_MESSAGE));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void processDocumentMultipleErrors() throws Exception {
        //setting different errors
        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.OUT_FILE_TO_MQ), INVALID_ERROR_MESSAGE + "#1"));
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.OUT_OUTBOUND), INVALID_ERROR_MESSAGE + "#2"));
        cm.getProcessingInfo().setProcessingException(TRANSPORT_ERROR_MESSAGE);

        when(oxalisErrorRecognizer.recognize(TRANSPORT_ERROR_MESSAGE)).thenReturn(SendingErrors.DATA_ERROR);

        controller.processMessage(cm);

        File subjectFile = new File(directory, "customer_id" + EmailController.EXT_SUBJECT);
        File toFile = new File(directory, "customer_id" + EmailController.EXT_TO);
        File bodyFile = new File(directory, "customer_id" + EmailController.EXT_BODY);

        //files created
        Assert.assertTrue(subjectFile.exists());
        Assert.assertTrue(toFile.exists());
        Assert.assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains("OUT_INVALID_EMAIL_SUBJECT"));
        content = Files.toString(toFile, Charsets.UTF_8);
        Assert.assertEquals(content, "outbound"); //should be the one used for outbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        Assert.assertTrue(content.contains(INVALID_ERROR_MESSAGE + "#1"));
        Assert.assertTrue(content.contains(INVALID_ERROR_MESSAGE + "#2"));
        Assert.assertTrue(content.contains(TRANSPORT_ERROR_MESSAGE));
        Assert.assertTrue(content.contains("ERRORS"));
    }

    @Test
    public void processDocumentNoErrors() {
        ContainerMessage cm = createTestContainerMessage();

        try {
            controller.processMessage(cm);
            fail("Exception not thrown when processing message");
        } catch (IllegalArgumentException good) {
            // ignore
        } catch (Exception ex) {
            fail("Different exception expected");
        }
    }

    //outbound
    private ContainerMessage createTestContainerMessage() {
        ContainerMessage cm = new ContainerMessage("metadata", directory + "/test.xml", Endpoint.TEST);
        DocumentInfo di = new DocumentInfo();
        di.setDocumentId("doc_id");
        di.setIssueDate("2017-07-18");
        di.setIssueTime("11:12:13");
        di.setArchetype(Archetype.EHF);
        di.setRecipientId("recipient_id");
        di.setRecipientName("recipient_name");
        di.setSenderId("customer_id");
        di.setSenderName("sender_name");
        cm.setDocumentInfo(di);
        return cm;
    }
}
