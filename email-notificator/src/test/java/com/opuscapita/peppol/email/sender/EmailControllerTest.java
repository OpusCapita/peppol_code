package com.opuscapita.peppol.email.sender;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.email.controller.BodyFormatter;
import com.opuscapita.peppol.email.controller.EmailController;
import com.opuscapita.peppol.email.model.CustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Gamans
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@ComponentScan(basePackages = {"com.opuscapita.peppol.email" , "com.opuscapita.peppol.commons.errors.oxalis" })
public class EmailControllerTest {
    //constants
    private static final String CUSTOMER_ID = "customer_id";
    private static final String OUTPUT_DIRECTORY = "/tmp" + File.separator;
    //mocks
    private CustomerRepository customerRepository = mock(CustomerRepository.class);
    private Customer customer  = mock(Customer.class);
    private ErrorHandler errorHandler = mock(ErrorHandler.class);
    //from config
    @Value("${peppol.email-notificator.out.invalid.subject}")
    private String outInvalidEmailSubject;
    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;
    @Value("${peppol.email-notificator.out.lookup-error.subject}")
    private String outLookupErrorEmailSubject;
    //spring
    @Autowired
    private BodyFormatter bodyFormatter;

    @Autowired
    private OxalisErrorRecognizer oxalisErrorRecognizer;

    @Test
    public void storeDocumentOutLookupError() throws Exception {
        System.out.println("Outbound look up error test");

        when(customer.getInboundEmails()).thenReturn("test_inbound@test.com");
        when(customer.getOutboundEmails()).thenReturn("test_outbound@test.com");
        when(customerRepository.findByIdentifier(any())).thenReturn(customer);

        EmailController controller = new EmailController(customerRepository,errorHandler,bodyFormatter, oxalisErrorRecognizer);
        ReflectionTestUtils.setField(controller,"directory", OUTPUT_DIRECTORY);
        ReflectionTestUtils.setField(controller,"outInvalidEmailSubject", outInvalidEmailSubject);
        ReflectionTestUtils.setField(controller,"inInvalidEmailSubject", inInvalidEmailSubject);
        ReflectionTestUtils.setField(controller,"outLookupErrorEmailSubject", outLookupErrorEmailSubject);

        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError( new Endpoint("test", ProcessType.OUT_OUTBOUND), "Problem with SMP lookup for participant TEST and document type TEST" ));
        controller.processMessage(cm);

        File subjectFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_SUBJECT);
        File toFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_TO);
        File bodyFile = new File(OUTPUT_DIRECTORY + File.separator + CUSTOMER_ID + EmailController.EXT_BODY);
        assertTrue(subjectFile.exists());
        assertTrue(toFile.exists());
        assertTrue(bodyFile.exists());
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        assertTrue(content.contains(outLookupErrorEmailSubject));
        content = Files.toString(toFile, Charsets.UTF_8);
        assertEquals(content,"test_outbound@test.com"); //should be the one used for outbound
        //clean ??
        cleanFiles(subjectFile,toFile,bodyFile);
    }

    @Test
    public void storeDocumentInValidationError() throws Exception {
        System.out.println("Inbound invalid error test");

        when(customer.getInboundEmails()).thenReturn("test_inbound_email@test.com");
        when(customer.getOutboundEmails()).thenReturn("test_outbound@test.com");
        when(customerRepository.findByIdentifier(any())).thenReturn(customer);

        EmailController controller = new EmailController(customerRepository,errorHandler,bodyFormatter, oxalisErrorRecognizer);
        ReflectionTestUtils.setField(controller,"directory", OUTPUT_DIRECTORY);
        ReflectionTestUtils.setField(controller,"outInvalidEmailSubject", outInvalidEmailSubject);
        ReflectionTestUtils.setField(controller,"inInvalidEmailSubject", inInvalidEmailSubject);
        ReflectionTestUtils.setField(controller,"outLookupErrorEmailSubject", outLookupErrorEmailSubject);

        ContainerMessage cm = createTestContainerMessage();
        cm.getDocumentInfo().getErrors().add(new DocumentError( new Endpoint("test", ProcessType.IN_IN), "Random error message" ));
        cm.setProcessingInfo(new ProcessingInfo(new Endpoint("test", ProcessType.IN_IN),"metadata"));
        controller.processMessage(cm);

        File subjectFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_SUBJECT);
        File toFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_TO);
        File bodyFile = new File(OUTPUT_DIRECTORY + File.separator + "recipient_id" + EmailController.EXT_BODY);
        assertTrue(subjectFile.exists());
        assertTrue(toFile.exists());
        assertTrue(bodyFile.exists());
        String content = Files.toString(subjectFile, Charsets.UTF_8);
        assertTrue(content.contains(inInvalidEmailSubject));
        content = Files.toString(toFile, Charsets.UTF_8);
        assertEquals(content,"test_inbound_email@test.com"); //should be the one used for outbound
        //clean
        cleanFiles(subjectFile,toFile,bodyFile);
    }
    //outbound
    private ContainerMessage createTestContainerMessage() {
        ContainerMessage cm = new ContainerMessage("metadata", "/tmp/test.xml", new Endpoint("test", ProcessType.TEST));
        DocumentInfo di = new DocumentInfo();
        di.setDocumentId("doc_id");
        di.setIssueDate("2017-07-18");
        di.setIssueTime("11:12:13");
        di.setRecipientId("recipient_id");
        di.setRecipientName("recipient_name");
        di.setSenderId(CUSTOMER_ID);
        di.setSenderName("sender_name");
        cm.setDocumentInfo(di);
        return cm;
    }

    private void cleanFiles(File... files){
        Arrays.stream(files).forEach(f -> f.delete());
    }
}
