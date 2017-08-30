package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.container.process.route.Route;
import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.outbound.OutboundApp;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import eu.peppol.identifier.PeppolDocumentTypeId;
import eu.peppol.identifier.PeppolProcessTypeId;
import eu.peppol.outbound.OxalisOutboundModule;
import eu.peppol.outbound.transmission.*;
import eu.peppol.security.CommonName;
import eu.peppol.smp.SmpLookupManager;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@TestPropertySource(locations = "classpath:application.yml", properties = {"OXALIS_HOME:src/test/resources/oxalis"})
@ComponentScan(basePackages = {"com.opuscapita.peppol.commons.container.document", "com.opuscapita.peppol.commons.container.xml"}, excludeFilters = @ComponentScan.Filter(value = {UblSender.class, OutboundController.class, OutboundApp.class}, type = FilterType.ASSIGNABLE_TYPE))
@EnableConfigurationProperties(value = {DocumentTemplates.class})
public class Svefaktura1SenderTest {

    // mocks
    private OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper = mock(OxalisOutboundModuleWrapper.class);
    private TransmissionRequestBuilder requestBuilder = mock(TransmissionRequestBuilder.class);
    private TransmissionRequest request = mock(TransmissionRequest.class);
    private SmpLookupManager.PeppolEndpointData peppolEndpointData = mock(SmpLookupManager.PeppolEndpointData.class);
    private OxalisOutboundModule oxalisOutboundModule = mock(OxalisOutboundModule.class);
    private Transmitter transmitter = mock(Transmitter.class);
    private TransmissionResponse response = mock(TransmissionResponse.class);

    @Autowired
    private DocumentLoader documentLoader;

    @Test
    public void testSvefakturaData() throws Exception {
        ArgumentCaptor<PeppolDocumentTypeId> documentTypeIdArgumentCaptor = ArgumentCaptor.forClass(PeppolDocumentTypeId.class);
        ArgumentCaptor<PeppolProcessTypeId> peppolProcessTypeIdArgumentCaptor = ArgumentCaptor.forClass(PeppolProcessTypeId.class);

        when(oxalisOutboundModuleWrapper.getTransmissionRequestBuilder(true)).thenReturn(requestBuilder);
        when(requestBuilder.documentType(documentTypeIdArgumentCaptor.capture())).thenReturn(requestBuilder);
        when(requestBuilder.processType(peppolProcessTypeIdArgumentCaptor.capture())).thenReturn(requestBuilder);
        when(requestBuilder.sender(any())).thenReturn(requestBuilder);
        when(requestBuilder.receiver(any())).thenReturn(requestBuilder);
        when(requestBuilder.trace(anyBoolean())).thenReturn(requestBuilder);
        when(requestBuilder.payLoad(any())).thenReturn(requestBuilder);
        when(requestBuilder.build()).thenReturn(request);
        when(request.getEndpointAddress()).thenReturn(peppolEndpointData);
        when(peppolEndpointData.getCommonName()).thenReturn(new CommonName("common_name"));
        when(oxalisOutboundModule.getTransmitter()).thenReturn(transmitter);
        when(transmitter.transmit(any())).thenReturn(response);

        Svefaktura1Sender sender = new Svefaktura1Sender(oxalisOutboundModuleWrapper);
        sender.oxalisOutboundModule = oxalisOutboundModule;

        String fileName = copyTestFile();
        ContainerMessage cm = loadFile(fileName);
        assertNotNull(cm.getDocumentInfo());

        Route route = new Route();
        route.getEndpoints().add("next");
        assertNotNull(cm.getProcessingInfo());
        cm.getProcessingInfo().setRoute(route);

        sender.send(cm);

        assertEquals("urn:sfti:documents:BasicInvoice:1:0::Invoice##urn:sfti:documents:BasicInvoice:1:0::1.0",
                documentTypeIdArgumentCaptor.getValue().toString());
        System.out.println(peppolProcessTypeIdArgumentCaptor.getValue().toString());
        assertEquals("urn:sfti:services:documentprocessing:BasicInvoice:1:0",
                peppolProcessTypeIdArgumentCaptor.getValue().toString());
    }

    private String copyTestFile() throws IOException {
        File tmp = File.createTempFile("svefaktura-unit-test-", "");

        try (InputStream inputStream = Svefaktura1SenderTest.class.getResourceAsStream("/sv1_valid.xml")) {
            IOUtils.copy(inputStream, new FileOutputStream(tmp));
        }

        tmp.deleteOnExit();

        return tmp.getAbsolutePath();
    }

    private ContainerMessage loadFile(String fileName) throws Exception {
        ContainerMessage cm = new ContainerMessage("meatdata", fileName, new Endpoint("test", ProcessType.TEST));

        try (InputStream inputStream = Svefaktura1SenderTest.class.getResourceAsStream("/sv1_valid.xml")) {
            cm.setDocumentInfo(documentLoader.load(inputStream, fileName, new Endpoint("test", ProcessType.TEST)));
        }

        return cm;
    }

}