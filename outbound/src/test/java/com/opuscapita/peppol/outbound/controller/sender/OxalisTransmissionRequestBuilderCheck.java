package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.outbound.OutboundApp;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import com.opuscapita.peppol.test.util.ContainerMessageTestLoader;
import no.difi.oxalis.commons.sbdh.SbdhParser;
import no.difi.oxalis.sniffer.PeppolStandardBusinessHeader;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.common.model.ParticipantIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Ignore("Stopped working with Oxalis 4.0, to review later")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application.yml", properties = {"OXALIS_HOME:src/test/resources/oxalis"})
@ComponentScan(basePackages = {"com.opuscapita.peppol.commons.container.document", "com.opuscapita.peppol.commons.container.xml"}, excludeFilters = @ComponentScan.Filter(value = {UblSender.class, OutboundController.class, OutboundApp.class}, type = FilterType.ASSIGNABLE_TYPE))
@EnableConfigurationProperties(value = {DocumentTemplates.class})
public class OxalisTransmissionRequestBuilderCheck {
    @Autowired
    private DocumentLoader documentLoader;

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkSbdhParsing() throws Exception {
        URL testFileUrl = this.getClass().getResource("/inconsistent_sender.xml");
        System.out.println(testFileUrl.toString());
        File testFile = getResourceFile(testFileUrl);
        Header parsedSbdh = SbdhParser.parse(new FileInputStream(testFile));
        assertNotNull(parsedSbdh);
        Header parsedPeppolStandardBusinessHeader = parsePayLoadAndDeduceSbdh(new FileInputStream(testFile));
        assertNotNull(parsedPeppolStandardBusinessHeader);
        PeppolStandardBusinessHeader suppliedHeaderFields = new PeppolStandardBusinessHeader();

        ContainerMessage containerMessage = createContainerMessageFromResourceFile("/inconsistent_sender.xml");
        DocumentInfo document = containerMessage.getDocumentInfo();

        ParticipantIdentifier receiverId = ParticipantIdentifier.of(document.getRecipientId());
        suppliedHeaderFields.setRecipientId(receiverId);

        ParticipantIdentifier senderId = ParticipantIdentifier.of(document.getSenderId());
        suppliedHeaderFields.setSenderId(senderId);

        DocumentTypeIdentifier documentTypeId = OxalisUtils.getPeppolDocumentTypeId(document);
        suppliedHeaderFields.setDocumentTypeIdentifier(documentTypeId);

        ProcessIdentifier processTypeId = ProcessIdentifier.of(document.getProfileId());
        suppliedHeaderFields.setProfileTypeIdentifier(processTypeId);

        List<String> overriddenHeaders = findRestrictedHeadersThatWillBeOverridden(parsedPeppolStandardBusinessHeader, suppliedHeaderFields);
        overriddenHeaders.forEach(System.out::println);
        assertEquals(1, overriddenHeaders.size());
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

    @SuppressWarnings({"Duplicates", "SameParameterValue"})
    @NotNull
    private ContainerMessage createContainerMessageFromResourceFile(String resourceFile) throws Exception {
        URL testFileUrl = this.getClass().getResource(resourceFile);
        System.out.println(testFileUrl.toString());
        File testFile = getResourceFile(testFileUrl);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        ContainerMessage containerMessage = ContainerMessageTestLoader.createContainerMessageFromFile(documentLoader, testFile);
        assertNotNull(containerMessage);
        return containerMessage;
    }

    private Header parsePayLoadAndDeduceSbdh(InputStream payload) {
        return SbdhParser.parse(payload);
    }

    private List<String> findRestrictedHeadersThatWillBeOverridden(Header parsed, PeppolStandardBusinessHeader supplied) {
        List<String> headers = new ArrayList<>();
        if (parsed.getSender() != null && supplied.getSenderId() != null && !supplied.getSenderId().equals(parsed.getSender())) {
            headers.add("SenderId");
        }

        if (parsed.getReceiver() != null && supplied.getRecipientId() != null && !supplied.getRecipientId().equals(parsed.getReceiver())) {
            headers.add("RecipientId");
        }

        if (parsed.getDocumentType() != null && supplied.getDocumentTypeIdentifier() != null && !supplied.getDocumentTypeIdentifier().equals(parsed.getDocumentType())) {
            headers.add("DocumentTypeIdentifier");
        }

        if (parsed.getProcess() != null && supplied.getProfileTypeIdentifier() != null && !supplied.getProfileTypeIdentifier().equals(parsed.getProcess())) {
            headers.add("ProfileTypeIdentifier");
        }

        return headers;
    }
}
