package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.outbound.OutboundApp;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import com.opuscapita.peppol.outbound.util.OxalisUtils;
import com.opuscapita.peppol.test.util.ContainerMessageTestLoader;
import eu.peppol.PeppolStandardBusinessHeader;
import eu.peppol.document.NoSbdhParser;
import eu.peppol.document.Sbdh2PeppolHeaderConverter;
import eu.peppol.document.SbdhFastParser;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.transmission.TransmissionRequestBuilder;
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
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

    @Test
    public void checkSbdhParsing() throws Exception {
        URL testFileUrl = this.getClass().getResource("/inconsistent_sender.xml");
        System.out.println(testFileUrl.toString());
        File testFile = getResourceFile(testFileUrl);
        SbdhFastParser sbdhFastParser = new SbdhFastParser();
        StandardBusinessDocumentHeader parsedSbdh = sbdhFastParser.parse(new FileInputStream(testFile));
        assertNotNull(parsedSbdh);
        PeppolStandardBusinessHeader parsedPeppolStandardBusinessHeader = parsePayLoadAndDeduceSbdh(parsedSbdh, new FileInputStream(testFile));
        assertNotNull(parsedPeppolStandardBusinessHeader);
        PeppolStandardBusinessHeader suppliedHeaderFields = new PeppolStandardBusinessHeader();

        ContainerMessage containerMessage = createContainerMessageFromResourceFile("/inconsistent_sender.xml");
        DocumentInfo document = containerMessage.getDocumentInfo();

        ParticipantId receiverId = new ParticipantId(document.getRecipientId());
        suppliedHeaderFields.setRecipientId(receiverId);

        ParticipantId senderId = new ParticipantId(document.getSenderId());
        suppliedHeaderFields.setSenderId(senderId);

        PeppolDocumentTypeId documentTypeId = OxalisUtils.getPeppolDocumentTypeId(document);
        suppliedHeaderFields.setDocumentTypeIdentifier(documentTypeId);

        PeppolProcessTypeId processTypeId = PeppolProcessTypeId.valueOf(document.getProfileId());
        suppliedHeaderFields.setProfileTypeIdentifier(processTypeId);

        List<String> overriddenHeaders = findRestricedHeadersThatWillBeOverridden(parsedPeppolStandardBusinessHeader, suppliedHeaderFields);
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

    private PeppolStandardBusinessHeader parsePayLoadAndDeduceSbdh(StandardBusinessDocumentHeader parsedSbdh, InputStream payload) {
        NoSbdhParser noSbdhParser = new NoSbdhParser();
        PeppolStandardBusinessHeader peppolSbdh;
        if (parsedSbdh != null) {
            peppolSbdh = Sbdh2PeppolHeaderConverter.convertSbdh2PeppolHeader(parsedSbdh);
        } else {
            peppolSbdh = noSbdhParser.parse(payload);
        }

        return peppolSbdh;
    }

    protected List<String> findRestricedHeadersThatWillBeOverridden(PeppolStandardBusinessHeader parsed, PeppolStandardBusinessHeader supplied) {
        List<String> headers = new ArrayList();
        if (parsed.getSenderId() != null && supplied.getSenderId() != null && !supplied.getSenderId().equals(parsed.getSenderId())) {
            headers.add("SenderId");
        }

        if (parsed.getRecipientId() != null && supplied.getRecipientId() != null && !supplied.getRecipientId().equals(parsed.getRecipientId())) {
            headers.add("RecipientId");
        }

        if (parsed.getDocumentTypeIdentifier() != null && supplied.getDocumentTypeIdentifier() != null && !supplied.getDocumentTypeIdentifier().equals(parsed.getDocumentTypeIdentifier())) {
            headers.add("DocumentTypeIdentifier");
        }

        if (parsed.getProfileTypeIdentifier() != null && supplied.getProfileTypeIdentifier() != null && !supplied.getProfileTypeIdentifier().equals(parsed.getProfileTypeIdentifier())) {
            headers.add("ProfileTypeIdentifier");
        }

        return headers;
    }
}
