package com.opuscapita.peppol.commons.errors;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

/**
 * Creates informational message out of container message for failed documents.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class ErrorFormatter {

    @NotNull
    public static String getErrorDescription(@NotNull ContainerMessage cm, @Nullable Exception e, @Nullable String additionalDetails) {
        String detailedDescription = "Failed to process message";

        detailedDescription += "\nFile name: " + cm.getFileName();

        if (StringUtils.isNotBlank(cm.getCustomerId())) {
            detailedDescription += "\nCustomerID: " + cm.getCustomerId();
        }

        if (e != null && StringUtils.isNotBlank(e.getMessage())) {
            detailedDescription += "\nError message: " + e.getMessage();
        }

        if (additionalDetails != null) {
            detailedDescription += "\nDetails: " + additionalDetails;
        }

        if (cm.getDocumentInfo() != null && cm.getDocumentInfo().getWarnings().size() > 0) {
            detailedDescription += "\nDocument warnings: " +
                    cm.getDocumentInfo().getWarnings().stream().map(DocumentWarning::toString).collect(Collectors.joining("\n\t"));
        }
        if (cm.getDocumentInfo() != null && cm.getDocumentInfo().getErrors().size() > 0) {
            detailedDescription += "\nDocument errors: " +
                    cm.getDocumentInfo().getErrors().stream().map(DocumentError::toString).collect(Collectors.joining("\n\t"));
        }

        if (cm.getProcessingInfo() == null) {
            cm.setProcessingInfo(new ProcessingInfo(new Endpoint("error_handler", ProcessType.UNKNOWN),
                    "Processing info missing in Container Message"));
        } else {
            detailedDescription += "\nLast processing status: " + cm.getProcessingInfo().getCurrentStatus();
        }

        String processingException = null;
        if (cm.getProcessingInfo() != null) {
            processingException = cm.getProcessingInfo().getProcessingException();
            if (StringUtils.isNotBlank(processingException)) {
                detailedDescription += "\nProcessing exception message: " + processingException;
            }
        }

        String exceptionMessage = exceptionMessageToString(e);
        if (exceptionMessage != null) {
            detailedDescription += "\nPlatform exception message: " + exceptionMessage;
        }

        String json = cm.convertToJson().replaceAll("\\{|\\}|\\\"|\\'", "");
        detailedDescription += "\nMessage content: \n" + json + "\n";

        if (e != null) {
            detailedDescription += "\n\nPlatform exception: " + ExceptionUtils.getStackTrace(e) + "\n";
        }

        return detailedDescription;
    }

    @NotNull
    public static String getErrorDescription(@Nullable String customerId, @Nullable Exception e, @Nullable String fileName,
                                        @Nullable String additionalDetails) {
        String detailedDescription = "Failed to process message";

        if (fileName != null) {
            detailedDescription += "\nFile name: " + fileName;
        }

        if (StringUtils.isNotBlank(customerId)) {
            detailedDescription += "\nCustomerID: " + customerId;
        }

        if (e != null && StringUtils.isNotBlank(e.getMessage())) {
            detailedDescription += "\nError message: " + e.getMessage();
        }

        String exceptionMessage = exceptionMessageToString(e);
        if (exceptionMessage != null) {
            detailedDescription += "\nPlatform exception message: " + exceptionMessage;
        }

        if (e != null) {
            detailedDescription += "\n\nPlatform exception: " + ExceptionUtils.getStackTrace(e) + "\n";
        }

        if (additionalDetails != null) {
            detailedDescription += "\n\nAdditional details: " + additionalDetails + "\n";
        }

        return detailedDescription;
    }

    @Nullable
    static String exceptionMessageToString(@Nullable Exception e) {
        if (e == null) {
            return null;
        }
        if (StringUtils.isBlank(e.getMessage())) {
            return null;
        }
        return e.getMessage();
    }
}
