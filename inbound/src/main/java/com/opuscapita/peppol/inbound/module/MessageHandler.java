package com.opuscapita.peppol.inbound.module;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.storage.Storage;
import no.difi.oxalis.api.inbound.InboundMetadata;
import no.difi.oxalis.commons.util.OxalisVersion;
import no.difi.vefa.peppol.common.model.Header;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Handles incoming message.
 *
 * @author Sergejs.Roze
 */
@Component
public class MessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${peppol.inbound.copy.directory:}")
    private String copyDirectory;

    @Value("${peppol.component.name}")
    private String componentName;

    private final Storage storage;
    private final ErrorHandler errorHandler;
    private final MessageSender messageSender;
    private final ContainerMessageSerializer containerMessageSerializer;
    private final Gson gson;

    @Autowired
    public MessageHandler(@NotNull Storage storage, @NotNull ErrorHandler errorHandler, @NotNull MessageSender messageSender,
                          @NotNull ContainerMessageSerializer containerMessageSerializer, @NotNull Gson gson) {
        this.storage = storage;
        this.errorHandler = errorHandler;
        this.messageSender = messageSender;
        this.containerMessageSerializer = containerMessageSerializer;
        this.gson = gson;
    }

    @NotNull
    String preProcess(String transmissionId, Header header, InputStream inputStream) throws IOException {
        String dataFile = storeTemporary(transmissionId, inputStream);
        // no unhandled exceptions after this line

        try {
            ContainerMessage cm = createContainerMessage(transmissionId, header, dataFile);
            String json = containerMessageSerializer.toJson(cm);
            FileUtils.write(new File(dataFile + ".cm"), json, Charset.forName("UTF-8"));
            logger.info("Container message prepared and stored as " + dataFile + ".cm");
        } catch (Exception e) {
            fail("Failed to store container message " + dataFile + ".cm", transmissionId, e);
        }

        return dataFile;
    }

    @SuppressWarnings("ConstantConditions")
    private ContainerMessage createContainerMessage(String transmissionId, Header header, String dataFile) {
        Endpoint endpoint = new Endpoint(componentName, ProcessType.IN_INBOUND);
        ContainerMessage cm = new ContainerMessage(gson.toJson(header), dataFile, endpoint);
        cm.getProcessingInfo().setTransactionId(transmissionId);
        cm.setStatus(cm.getProcessingInfo().getSource(), "received");
        EventingMessageUtil.reportEvent(cm, "received file");
        return cm;
    }

    // this is the only method that allowed to throw an exception which will be propagated to the sending party
    private String storeTemporary(String transmissionId, InputStream inputStream) throws IOException {
        try {
            String result = storage.storeTemporary(inputStream, transmissionId + ".xml", copyDirectory);
            logger.info("Received message stored as " + result);
            return result;
        } catch (Exception e) {
            fail("Failed to store message " + transmissionId + ".xml", transmissionId, e);
            throw new IOException("Failed to store message " + transmissionId + ".xml: " + e.getMessage(), e);
        }
    }

    private void fail(String message, String transmissionId, Exception e) {
        logger.error(message, e);
        try {
            errorHandler.reportWithoutContainerMessage(null, e, message, transmissionId, transmissionId + ".xml");
        } catch (Exception ex) {
            logger.error("Failed to report error '" + message + "'", ex);
        }
    }

    @SuppressWarnings("ConstantConditions")
    void process(InboundMetadata inboundMetadata, Path payloadPath) {
        Header header = inboundMetadata.getHeader();
        ContainerMessage cm = createContainerMessage(header.getIdentifier().toString(), header, payloadPath.toString());
        cm.getProcessingInfo().setSourceMetadata(metadataToJson(header, inboundMetadata));

        messageSender.send(cm);
    }

    private String metadataToJson(Header header, InboundMetadata inboundMetadata) {
        X509Certificate certificate = inboundMetadata.getCertificate();
        X500Principal principal = certificate.getSubjectX500Principal();

        return "{ \"PeppolMessageMetaData\" :\n  {\n" +
                createJsonPair("messageId", header.getIdentifier()) +
                createJsonPair("recipientId", header.getReceiver().getIdentifier()) +
                createJsonPair("recipientSchemeId", header.getReceiver().getScheme()) +
                createJsonPair("senderId", header.getSender().getIdentifier()) +
                createJsonPair("senderSchemeId", header.getSender().getScheme()) +
                createJsonPair("documentTypeIdentifier", header.getDocumentType().getIdentifier()) +
                createJsonPair("profileTypeIdentifier", header.getProcess().getIdentifier()) +
                createJsonPair("sendingAccessPoint", principal == null ? null : principal.getName()) +
                createJsonPair("receivingAccessPoint", "Opuscapita AP") +
                createJsonPair("protocol", inboundMetadata.getProtocol().getIdentifier()) +
                createJsonPair("sendersTimeStamp", inboundMetadata.getTimestamp()) +
                createJsonPair("receivedTimeStamp", new Date()) +
                createJsonPair("sendingAccessPointPrincipal", principal) +
                createJsonPair("transmissionId", header.getIdentifier()) +
                createJsonPair("buildUser", OxalisVersion.getUser()) +
                createJsonPair("buildDescription", OxalisVersion.getBuildDescription()) +
                createJsonPair("buildTimeStamp", OxalisVersion.getBuildTimeStamp()) +
                "    \"oxalis\" : \"" + OxalisVersion.getVersion() + "\"\n" +
                "  }\n}\n";
    }

    private static String createJsonPair(String key, Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"").append(key).append("\" : ");
        if (value == null) {
            sb.append("null,\n");
        } else {
            sb.append("\"").append(value.toString()).append("\",\n");
        }

        return sb.toString();
    }
}
