package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds the whole data exchange bean inside the application.<br/>
 * Consists of two parts - document information and process information.
 * The first one describes the real content of the document while the second is responsible for holding Peppol AP specific data.
 *
 * @author Sergejs.Roze
 */
public class ContainerMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ProcessingInfo processingInfo;
    private String fileName;
    private DocumentInfo documentInfo;

    /**
     * Instantiates new container of the message.
     *
     * @param metadata some voluntary data provided by the originator of the message
     * @param fileName the file name to be used for the document
     * @param source the originator of the message
     */
    public ContainerMessage(@NotNull String metadata, @NotNull String fileName, @NotNull Endpoint source) {
        this.processingInfo = new ProcessingInfo(source, metadata);
        this.fileName = fileName;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @NotNull
    public ContainerMessage setFileName(@NotNull String fileName) {
        this.fileName = fileName;
        return this;
    }

    @NotNull
    public ProcessingInfo getProcessingInfo() {
        return processingInfo;
    }

    @Nullable
    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    @NotNull
    public ContainerMessage setDocumentInfo(@NotNull DocumentInfo documentInfo) {
        this.documentInfo = documentInfo;
        return this;
    }

    /**
     * Checks whether this document is inbound or outbound. All documents that originated from Peppol
     * network are considered being inbound, all others - outbound.
     *
     * @return true if this is inbound document
     */
    public boolean isInbound() {
        return processingInfo.isInbound();
    }

    /**
     * Returns customer ID depending on the direction of the message, either sender or recipient ID.
     */
    @Nullable
    public String getCustomerId() {
        if (getDocumentInfo() == null) {
            return null;
        }
        return isInbound() ? getDocumentInfo().getRecipientId() : getDocumentInfo().getSenderId();
    }

    /**
     * Serializes object to JSON.
     *
     * @return the JSON serialized version of this object
     */
    public String convertToJson() {
        return new Gson().toJson(this);
    }

    public byte[] convertToJsonByteArray() {
        return convertToJson().getBytes();
    }

    /**
     * Returns correlation ID if any or file name as correlation ID. Data is being read from processing status object.
     *
     * @return Returns correlation ID if any or file name as correlation ID
     */
    @NotNull
    public String getCorrelationId() {
        if (StringUtils.isNotBlank(processingInfo.getCorrelationId())) {
            return processingInfo.getCorrelationId();
        }
        return fileName;
    }

    @Override
    @NotNull
    public String toString() {
        String result = fileName + " from " + processingInfo.getSource();
        if (documentInfo != null) {
            result += " [" + documentInfo.getClass().getName() + "]";
        }
        if (processingInfo.getRoute() != null) {
            result += ": " + processingInfo.getRoute();
        }

        return result;
    }

    public void setStatus(@NotNull Endpoint endpoint, @NotNull String status) {
        processingInfo.setCurrentStatus(endpoint, status);
    }

    @Nullable
    public String popRoute() {
        if (processingInfo.getRoute() != null) {
            return processingInfo.getRoute().pop();
        }
        return null;
    }

}
