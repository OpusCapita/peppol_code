package eu.peppol.persistence;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.storage.StorageImpl;
import com.opuscapita.peppol.inbound.InboundErrorHandler;
import com.opuscapita.peppol.inbound.InboundMessageSender;
import com.opuscapita.peppol.inbound.InboundProperties;
import eu.peppol.PeppolMessageMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

import static com.opuscapita.peppol.inbound.InboundProperties.INBOUND_OUTPUT_DIR;


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

    @SuppressWarnings("ConstantConditions")
    public ExtendedMessageRepository() {
        super(new File(properties.getProperty(INBOUND_OUTPUT_DIR)));
        errorHandler = new InboundErrorHandler(properties);
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
                    .storeTemporary(inputStream, normalizeFilename(metadata.getTransmissionId().toString()) + ".xml");
            logger.info("Message stored as: " + dataFile);
        } catch (Exception e) {
            logger.error("Failed to store incoming message", e);
            errorHandler.report("Failed to store incoming message", e.getMessage(), metadata.getRecipientId().toString());
            throw new OxalisMessagePersistenceException(metadata, e);
        }

        // send file to MQ, no exception to sending AP here anymore, file already available
        try {
            String metadataString = getHeadersAsJSON(metadata);
            logger.debug("Message metadata: " + metadataString);

            ContainerMessage cm = prepareMessage(dataFile, metadataString);
            new InboundMessageSender(properties).send(cm);
        } catch (Exception e) {
            logger.error("Failed to report file " + dataFile + " to MQ: ", e);
            errorHandler.report("Failed to report received file to message queue", e.getMessage(), metadata.getRecipientId().toString());
        }
    }

    private ContainerMessage prepareMessage(String fileName, String metadata) {
        return new ContainerMessage(metadata, fileName, Endpoint.PEPPOL);
    }

}
