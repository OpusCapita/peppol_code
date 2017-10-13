package com.opuscapita.peppol.email.sender;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorsList;
import com.opuscapita.peppol.commons.mq.RabbitMq;
import com.opuscapita.peppol.email.EmailNotificatorApp;
import com.opuscapita.peppol.email.controller.EmailController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Gamans
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application.yml")
@ComponentScan(basePackages = {"com.opuscapita.peppol.email", "com.opuscapita.peppol.commons.errors.oxalis"}, excludeFilters = @ComponentScan.Filter(value = {EmailNotificatorApp.class, RabbitMq.class}, type = FilterType.ASSIGNABLE_TYPE))
@EnableConfigurationProperties(OxalisErrorsList.class)
public class EmailControllerTest {
    //constants
    private static final String CUSTOMER_ID = "customer_id";
    private static final String OUTPUT_DIRECTORY = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath() + File.separator;
    private static final String INVALID_ERROR_MESSAGE = "INVALID error message";
    private static final String TRANSPORT_ERROR_MESSAGE = "Problem with SMP lookup for participant TEST and document type TEST";

    //from config
    @Value("${peppol.email-notificator.out.invalid.subject}")
    private String outInvalidEmailSubject;
    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;
    @Value("${peppol.email-notificator.out.lookup-error.subject}")
    private String outLookupErrorEmailSubject;
    //spring
    @Autowired
    private TestConfig testConfig;

    @Autowired
    private EmailController controller;

    @Test
    public void processDocumentOutLookupError() throws Exception {
        System.out.println("Outbound look up error test");

        ContainerMessage cm = createTestContainerMessage();
        cm.getProcessingInfo().setProcessingException(TRANSPORT_ERROR_MESSAGE);
        controller.processMessage(cm);

        File subjectFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_SUBJECT);
        File toFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_TO);
        File bodyFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_BODY);

        //files created
        assertTrue(subjectFile.exists());
        assertTrue(toFile.exists());
        assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        assertTrue(content.contains(outLookupErrorEmailSubject));
        content = Files.toString(toFile, Charsets.UTF_8);
        assertEquals(content, testConfig.OUTBOUND_EMAIL); //should be the one used for outbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        assertTrue(content.contains(TRANSPORT_ERROR_MESSAGE));

        //clean
        cleanFiles(subjectFile, toFile, bodyFile);
    }

    @Test
    public void processDocumentInValidationError() throws Exception {
        System.out.println("Inbound invalid error test");

        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.IN_IN), INVALID_ERROR_MESSAGE));
        cm.setProcessingInfo(new ProcessingInfo(new Endpoint("test", ProcessType.IN_IN), "metadata"));
        controller.processMessage(cm);

        File subjectFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_SUBJECT);
        File toFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_TO);
        File bodyFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_BODY);

        //files created
        assertTrue(subjectFile.exists());
        assertTrue(toFile.exists());
        assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        assertTrue(content.contains(inInvalidEmailSubject));
        content = Files.toString(toFile, Charsets.UTF_8);
        assertEquals(content, testConfig.INBOUND_EMAIL); //should be the one used for inbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        assertTrue(content.contains(INVALID_ERROR_MESSAGE));

        //clean
        cleanFiles(subjectFile, toFile, bodyFile);
    }

    @Test
    public void processDocumentMultipleErrors() throws Exception {
        System.out.println("Multiple Errors test");

        //setting different errors
        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.OUT_FILE_TO_MQ), INVALID_ERROR_MESSAGE + "#1"));
        cm.getDocumentInfo().getErrors().add(new DocumentError(new Endpoint("test", ProcessType.OUT_OUTBOUND), INVALID_ERROR_MESSAGE + "#2"));
        cm.getProcessingInfo().setProcessingException(TRANSPORT_ERROR_MESSAGE);

        controller.processMessage(cm);

        File subjectFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_SUBJECT);
        File toFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_TO);
        File bodyFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_BODY);

        //files created
        assertTrue(subjectFile.exists());
        assertTrue(toFile.exists());
        assertTrue(bodyFile.exists());

        //check content
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        assertTrue(content.contains(outLookupErrorEmailSubject));
        content = Files.toString(toFile, Charsets.UTF_8);
        assertEquals(content, testConfig.OUTBOUND_EMAIL); //should be the one used for outbound

        content = Files.toString(bodyFile, Charsets.UTF_8);
        assertTrue(content.contains(INVALID_ERROR_MESSAGE + "#1"));
        assertTrue(content.contains(INVALID_ERROR_MESSAGE + "#2"));
        assertTrue(content.contains(TRANSPORT_ERROR_MESSAGE));
        assertTrue(content.contains("ERRORS"));

        //clean
        cleanFiles(subjectFile, toFile, bodyFile);
    }

    @Test
    public void processDocumentNoErrors() {
        System.out.println("Inbound no errors test");

        ContainerMessage cm = createTestContainerMessage();

        try {
            controller.processMessage(cm);
            fail("Exception not thrown when processing message");
        } catch (IllegalArgumentException good) {
        } catch (Exception ex) {
            fail("Different exception expected!");
        }
    }

    //outbound
    private ContainerMessage createTestContainerMessage() {
        ContainerMessage cm = new ContainerMessage("metadata", OUTPUT_DIRECTORY + "/test.xml", new Endpoint("test", ProcessType.TEST));
        DocumentInfo di = new DocumentInfo();
        di.setDocumentId("doc_id");
        di.setIssueDate("2017-07-18");
        di.setIssueTime("11:12:13");
        di.setArchetype(Archetype.EHF);
        di.setRecipientId("recipient_id");
        di.setRecipientName("recipient_name");
        di.setSenderId(CUSTOMER_ID);
        di.setSenderName("sender_name");
        cm.setDocumentInfo(di);
        return cm;
    }

    private void cleanFiles(File... files) {
        Arrays.stream(files).forEach(f -> f.delete());
    }
}
