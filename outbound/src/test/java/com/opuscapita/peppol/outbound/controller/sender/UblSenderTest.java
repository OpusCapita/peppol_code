package com.opuscapita.peppol.outbound.controller.sender;

import com.google.common.io.Files;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.outbound.OutboundApp;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import com.opuscapita.peppol.test.util.ContainerMessageTestLoader;
import eu.peppol.outbound.transmission.OxalisOutboundModuleWrapper;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
import org.apache.commons.io.Charsets;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@Ignore("Test goes crazy, has to review it")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application.yml", properties = {"OXALIS_HOME:src/test/resources/oxalis"})
@ComponentScan(basePackages = {"com.opuscapita.peppol.commons.container.document", "com.opuscapita.peppol.commons.container.xml"}, excludeFilters = @ComponentScan.Filter(value = {UblSender.class, OutboundController.class, OutboundApp.class}, type = FilterType.ASSIGNABLE_TYPE))
@EnableConfigurationProperties(value = {DocumentTemplates.class})
public class UblSenderTest {
    @Autowired
    private DocumentLoader documentLoader;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void send() throws Exception {
        System.out.println(new File(".").getAbsolutePath());
        System.setProperty("OXALIS_HOME","src/test/resources/oxalis");
        System.out.println(System.getenv("OXALIS_HOME"));
        URL testFileUrl = this.getClass().getResource("/EHF_profile-bii05_invoice.xml");
        System.out.println(testFileUrl.toString());
        File testFile = getResourceFile(testFileUrl);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        ContainerMessage containerMessage = ContainerMessageTestLoader.createContainerMessageFromFile(documentLoader, testFile);
        assertNotNull(containerMessage);

        UblSender ublSender = new UblSenderWrapper(new OxalisOutboundModuleWrapper());
        ublSender.initialize();
        try {
            ublSender.send(containerMessage);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("POST"));
        }
        File jsonFileFromStage = getResourceFile(this.getClass().getResource("/msg-to-outbound.json.json"));
        String jsonContent = Files.readLines(jsonFileFromStage, Charsets.UTF_8).stream().collect(Collectors.joining());
        containerMessage = new ContainerMessageSerializer().fromJson(jsonContent);
        containerMessage.setFileName(getResourceFile(this.getClass().getResource("/EHF_profile-bii05_invoice_OC_3.xml")).getAbsolutePath());
        try {
            ublSender.send(containerMessage);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("POST"));
        }
    }

    @NotNull
    private File getResourceFile(URL testFileUrl) {
        File testFile;
        try {
            testFile = new File(testFileUrl.toURI());
        } catch (URISyntaxException e) {
            testFile = new File(testFileUrl.getPath());
        }
        return testFile;
    }

    private class UblSenderWrapper extends UblSender {

        UblSenderWrapper(OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper) {
            super(oxalisOutboundModuleWrapper);
        }

        @Override
        public void initialize() {
            oxalisOutboundModule = oxalisOutboundModuleWrapper.getOxalisOutboundModule();
        }

        @Override
        protected TransmissionRequestBuilder getTransmissionRequestBuilder() {
            return oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true);
        }
    }

}