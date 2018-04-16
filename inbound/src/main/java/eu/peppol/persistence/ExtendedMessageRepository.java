package eu.peppol.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.storage.StorageImpl;
import com.opuscapita.peppol.inbound.InboundErrorHandler;
import com.opuscapita.peppol.inbound.InboundMessageSender;
import com.opuscapita.peppol.inbound.InboundProperties;
import com.opuscapita.peppol.inbound.MetadataUtils;
import eu.peppol.PeppolMessageMetaData;
import eu.peppol.evidence.TransmissionEvidence;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.TransmissionId;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.opuscapita.peppol.inbound.InboundProperties.*;


/**
 * Replaces standard Oxalis storage with the one that also sends file name further to MQ.<br/>
 * Moved to package eu.peppol.persistence to be able to access internal data of the parent class.<br/>
 *
 * @author Sergejs.Roze
 */
public class ExtendedMessageRepository extends SimpleMessageRepository {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedMessageRepository.class);
    private static final InboundProperties properties = new InboundProperties();
    private final InboundErrorHandler errorHandler;
    private final Gson gson;

    @SuppressWarnings("ConstantConditions")
    public ExtendedMessageRepository() {
        super(new File(properties.getProperty(INBOUND_OUTPUT_DIR)));
        errorHandler = new InboundErrorHandler(properties);
        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void saveInboundMessage(PeppolMessageMetaData metadata, InputStream inputStream) throws OxalisMessagePersistenceException {
        logger.info("Received message with Transmission ID = " + metadata.getTransmissionId());

        // store file locally
        String dataFile;
        try {
            dataFile = new StorageImpl()
                    .setShortTermDirectory(properties.getProperty(INBOUND_OUTPUT_DIR))
                    .storeTemporary(inputStream,
                            normalizeFilename(metadata.getTransmissionId().toString()) + ".xml",
                            properties.getProperty(INBOUND_COPY_DIR));
            logger.info("Message stored as: " + dataFile);
        } catch (Exception e) {
            logger.error("Failed to store incoming message", e);
            errorHandler.report("Failed to store incoming message", e.getMessage(), metadata.getRecipientId().toString());
            throw new OxalisMessagePersistenceException(metadata, e);
        }
        ContainerMessage cm = null;
        String metadataString = null;
        // send file to MQ, no exception to sending AP here anymore, file already available
        try {
            metadataString = MetadataUtils.getHeadersAsJson(metadata);
            logger.debug("Message metadata: " + metadataString);


            cm = prepareMessage(dataFile, metadataString, properties.getProperty(COMPONENT_NAME));
            cm.getProcessingInfo().setTransactionId(metadata.getTransmissionId().toString());
            cm.setStatus(cm.getProcessingInfo().getSource(), "received");
            EventingMessageUtil.reportEvent(cm, "received file");
            new InboundMessageSender(properties).send(cm);
        } catch (Exception e) {
            logger.error("Failed to report file " + dataFile + " to MQ: ", e);
            errorHandler.report("Failed to report received file to message queue", e.getMessage(), metadata.getRecipientId().toString());
            if (metadataString != null) {
                logger.error("Metadata dump: " + metadataString);
            }
            if (cm != null) {
                logger.error("ContainerMessage dump: " + gson.toJson(cm));
            }
        }
    }

    @Override
    public void saveTransportReceipt(TransmissionEvidence transmissionEvidence, PeppolMessageMetaData peppolMessageMetaData) {
        logger.info("Saving the transport receipt. This method has been overridden btw");
        File messageDirectory = this.prepareMessageDirectory(properties.getProperty(INBOUND_OUTPUT_DIR), peppolMessageMetaData.getRecipientId(), peppolMessageMetaData.getSenderId());
        File evidenceFullPath = computeEvidenceFileName(peppolMessageMetaData.getTransmissionId(), messageDirectory);

        try {
            IOUtils.copy(transmissionEvidence.getInputStream(), new FileOutputStream(evidenceFullPath));
        } catch (IOException ioEx) {
            throw new IllegalStateException("Unable to write transmission evidence to " + evidenceFullPath.getAbsolutePath() + ": " + ioEx.getMessage(), ioEx);
        }

        logger.info("Transmission evidence written to " + evidenceFullPath.getAbsolutePath());
    }

    private ContainerMessage prepareMessage(String fileName, String metadata, String componentName) {
        return new ContainerMessage(metadata, fileName, new Endpoint(componentName, ProcessType.IN_INBOUND));
    }

    private File computeEvidenceFileName(TransmissionId transmissionId, File messageDirectory) {
        String evidenceFileName = normalizeFilename(transmissionId.toString() + "-rem.xml");
        return new File(messageDirectory, evidenceFileName);
    }

    @Override
    File prepareMessageDirectory(String inboundMessageStore, ParticipantId recipient, ParticipantId sender) {
        String path = String.format("%s/%s", normalizeFilename(recipient.stringValue()), normalizeFilename(sender.stringValue()));
        File messageDirectory = new File(inboundMessageStore, path);
        if (!messageDirectory.exists() && !messageDirectory.mkdirs()) {
            throw new IllegalStateException("Unable to create directory " + messageDirectory.toString());
        } else if (messageDirectory.isDirectory() && messageDirectory.canWrite()) {
            return messageDirectory;
        } else {
            throw new IllegalStateException("Directory " + messageDirectory + " does not exist, or there is no access");
        }
    }

}
