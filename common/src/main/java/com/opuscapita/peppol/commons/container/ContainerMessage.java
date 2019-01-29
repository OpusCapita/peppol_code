package com.opuscapita.peppol.commons.container;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.apache.commons.io.FilenameUtils;
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

    private static final long serialVersionUID = -5450780856722626102L;

    @Since(1.0) private String fileName;
    @Since(1.0) private String originalFileName = "";
    @Since(1.0) private DocumentInfo documentInfo;
    @Since(1.0) private ProcessingInfo processingInfo;

    public ContainerMessage() {
    }

    /**
     * Instantiates new container of the message.
     *
     * @param fileName the file name to be used for the document
     * @param source   the originator of the message
     */
    public ContainerMessage(@NotNull String fileName, @NotNull Endpoint source) {
        this.fileName = fileName;
        this.processingInfo = new ProcessingInfo(source);
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
    public String getOriginalFileName() {
        if (StringUtils.isBlank(originalFileName)) {
            return FilenameUtils.getBaseName(fileName);
        }
        return originalFileName;
    }

    public void setOriginalFileName(@NotNull String originalFileName) {
        this.originalFileName = FilenameUtils.getBaseName(originalFileName);
    }

    @Nullable
    public ProcessingInfo getProcessingInfo() {
        return processingInfo;
    }

    public void setProcessingInfo(@NotNull ProcessingInfo processingInfo) {
        this.processingInfo = processingInfo;
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

    public boolean isOutbound() {
        return !this.isInbound();
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

    @NotNull
    public String getCorrelationId() {
        if (processingInfo == null || StringUtils.isBlank(processingInfo.getTransactionId())) {
            return fileName;
        }
        return processingInfo.getTransactionId();
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

    public void addError(@NotNull DocumentError error) {
        documentInfo.getErrors().add(error);
    }

    public void addWarning(@NotNull DocumentWarning warning) {
        documentInfo.getWarnings().add(warning);
    }

    public void addError(@NotNull String message) {
        addError(new DocumentError(processingInfo.getCurrentEndpoint(), message));
    }

    @SuppressWarnings("SimplifiableIfStatement")
    public boolean hasErrors() {
        if (documentInfo != null && !documentInfo.getErrors().isEmpty()) {
            return true;
        }
        return (processingInfo != null && processingInfo.getProcessingException() != null);
    }

    @Override
    @NotNull
    public String toString() {
        String result = fileName;
        if (processingInfo != null) {
            result += " from " + processingInfo.getSource();
        }
        if (documentInfo != null) {
            result += " [" + documentInfo.getClass().getName() + "]";
        }
        if (processingInfo != null && processingInfo.getRoute() != null) {
            result += ": " + processingInfo.getRoute();
        }

        return result;
    }

    public String toLog() {
        String result = "[file: {filename}, customer: {customer}, status: {status}, endpoint: {endpoint}]";
        result = result.replace("{filename}", "{" + fileName + "}");

        String customerId = getCustomerId();
        result = result.replace("{customer}", "{" + (StringUtils.isBlank(customerId) ? "unknown" : customerId) + "}");

        String status = processingInfo == null ? "unknown" : processingInfo.getCurrentStatus();
        result = result.replace("{status}", "{" + (StringUtils.isBlank(status) ? "unknown" : status) + "}");

        Endpoint endpoint = processingInfo == null ? null : processingInfo.getCurrentEndpoint();
        result = result.replace("{endpoint}", "{" + (endpoint == null ? "unknown" : endpoint.getType()) + "}");

        return result;
    }
}
