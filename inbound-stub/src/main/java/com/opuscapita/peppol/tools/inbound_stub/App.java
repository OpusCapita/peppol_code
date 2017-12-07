package com.opuscapita.peppol.tools.inbound_stub;

import com.opuscapita.peppol.inbound.InboundProperties;
import eu.peppol.PeppolMessageMetaData;
import eu.peppol.identifier.*;
import eu.peppol.persistence.ExtendedMessageRepository;
import eu.peppol.persistence.OxalisMessagePersistenceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.UUID;

public class App {

    public static void main(String... args) throws URISyntaxException {
        File propertyFile = new File("inbound-stub/src/main/resources/application.properties");
        System.out.println(propertyFile.getAbsolutePath() + " exists: " + propertyFile.exists());
        InboundProperties.setPropertiesFilePath(propertyFile.getAbsolutePath());
        ExtendedMessageRepository extendedMessageRepository = new ExtendedMessageRepository();
        Arrays.asList(args).forEach(file -> {
            PeppolMessageMetaData peppolMessageMetaData = new PeppolMessageMetaData();
            String messageId = UUID.randomUUID().toString();
            peppolMessageMetaData.setMessageId(messageId);
            peppolMessageMetaData.setTransmissionId(new TransmissionId(messageId));
            peppolMessageMetaData.setRecipientId(new ParticipantId("9908:recipient"));
            peppolMessageMetaData.setSenderId(new ParticipantId("9908:sender"));
            peppolMessageMetaData.setDocumentTypeIdentifier(new PeppolDocumentTypeId("cenbii-procid-ubl", "Invoice", new CustomizationIdentifier("urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol4a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0::2.1"), "2.1"));
            peppolMessageMetaData.setProfileTypeIdentifier(new PeppolProcessTypeId("urn:www.cenbii.eu:profile:bii04:ver2.0"));
            try {
                InputStream testFileInputStream = new FileInputStream(file);
                extendedMessageRepository.saveInboundMessage(peppolMessageMetaData, testFileInputStream);
            } catch (OxalisMessagePersistenceException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });


    }
}
