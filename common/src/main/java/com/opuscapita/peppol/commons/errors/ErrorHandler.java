package com.opuscapita.peppol.commons.errors;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Daniil on 19.07.2016.
 * <p>
 * TODO separate between our errors that should be handled by DTP and service desk related stuff
 */
@Component
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    private final ServiceNow serviceNowRest;

    @Autowired
    public ErrorHandler(@NotNull ServiceNow serviceNowRest) {
        this.serviceNowRest = serviceNowRest;
    }

    public void reportWithContainerMessage(@NotNull ContainerMessage cm, @Nullable Exception e, @NotNull String shortDescription) {
        reportWithContainerMessage(cm, e, shortDescription, null);
    }

    public void reportWithContainerMessage(@NotNull ContainerMessage cm, @Nullable Exception e, @NotNull String shortDescription, @Nullable String additionalDetails) {
        createTicketFromContainerMessage(cm, e, shortDescription, additionalDetails);
    }
    public void reportWithoutContainerMessage(@Nullable String customerId, @Nullable Exception e, @NotNull String shortDescription,
                                              @Nullable String correlationId, @Nullable String fileName) {
        reportWithoutContainerMessage(customerId, e, shortDescription, correlationId, fileName, null);

    }

    public void reportWithoutContainerMessage(@Nullable String customerId, @Nullable Exception e, @NotNull String shortDescription,
                                              @Nullable String correlationId, @Nullable String fileName, @Nullable String additionalDetails) {
        createTicketWithoutContainerMessage(customerId, e, fileName, shortDescription, correlationId, additionalDetails);
    }

    private void createTicketWithoutContainerMessage(@Nullable String customerId, @Nullable Exception e, @Nullable String fileName,
                                                     @NotNull String shortDescription, @Nullable String correlationId, String additionalDetails) {
        String detailedDescription = "Failed to process message";

        if (fileName != null) {
            detailedDescription += "\nFile name: " + fileName;
        } else {
            fileName = "n/a";
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

        if(additionalDetails != null) {
            detailedDescription += "\n\nAdditional details: " + additionalDetails + "\n";
        }

        if (StringUtils.isBlank(correlationId)) {
            correlationId = fileName;
            if (StringUtils.isNotBlank(exceptionMessage)) {
                correlationId += exceptionMessage;
            }
        }
        try {
            correlationId = correlationIdDigest(correlationId);
        } catch (NoSuchAlgorithmException e1) {
            logger.warn("Failed to create SHA-1 has of correlation id");
            e1.printStackTrace();
        }
        createTicket(shortDescription, detailedDescription, correlationId, customerId, fileName);
    }

    private void createTicketFromContainerMessage(@NotNull ContainerMessage cm, @Nullable Exception e, @NotNull String shortDescription, String additionalDetails) {
        String detailedDescription = "Failed to process message";

        detailedDescription += "\nFile name: " + cm.getFileName();

        if (StringUtils.isNotBlank(cm.getCustomerId())) {
            detailedDescription += "\nCustomerID: " + cm.getCustomerId();
        }

        if (e != null && StringUtils.isNotBlank(e.getMessage())) {
            detailedDescription += "\nError message: " + e.getMessage();
        }

        Exception processingException = null;
        if (cm.getBaseDocument() instanceof InvalidDocument) {
            detailedDescription += "\nProcessing error: " + ((InvalidDocument) cm.getBaseDocument()).getReason();

            if (exceptionMessageToString(((InvalidDocument) cm.getBaseDocument()).getException()) != null) {
                processingException = ((InvalidDocument) cm.getBaseDocument()).getException();
                detailedDescription += "\nProcessing exception message: " + exceptionMessageToString(processingException);
            }
        }

        detailedDescription += "\nLast processing status: " + cm.getProcessingStatus();

        String exceptionMessage = exceptionMessageToString(e);
        if (exceptionMessage != null) {
            detailedDescription += "\nPlatform exception message: " + exceptionMessage;
        }

        String json = cm.convertToJson().replaceAll("\\{|\\}|\\\"|\\'", "");
        detailedDescription += "\nMessage content: \n" + json + "\n";

        if (e != null) {
            detailedDescription += "\n\nPlatform exception: " + ExceptionUtils.getStackTrace(e) + "\n";
        }
        if (processingException != null) {
            detailedDescription += "\n\nProcessing exception: " + ExceptionUtils.getStackTrace(processingException);
        }

        if(additionalDetails != null) {
            detailedDescription += "\n\nAdditional details: " + additionalDetails;
        }

        createTicket(shortDescription, detailedDescription, cm.getCorrelationId() + cm.getProcessingStatus(),
                cm.getCustomerId(), cm.getFileName());
    }

    @Nullable
    private String exceptionMessageToString(@Nullable Exception e) {
        if (e == null) {
            return null;
        }
        if (StringUtils.isBlank(e.getMessage())) {
            return null;
        }
        return e.getMessage();
    }

    private void createTicket(@NotNull String shortDescription, @NotNull String detailedDescription,
                              @NotNull String correlationId, @Nullable String customerId, @NotNull String fileName) {
        if (StringUtils.isBlank(customerId)) {
            customerId = "n/a";
        }
        try {
            String md5 = new String(MessageDigest.getInstance("MD5").digest(correlationId.getBytes()));
            SncEntity ticket = new SncEntity(shortDescription, detailedDescription, md5, customerId, 0);
            serviceNowRest.insert(ticket);
            logger.info("ServiceNow ticket created for " + fileName + " about " + shortDescription);
        } catch (Exception e) {
            logger.error("Failed to create SNC ticket for customer: " + customerId + ", file: " + fileName +
                    " about " + shortDescription + " with data: " + detailedDescription, e);
        }
    }

    @NotNull
    String correlationIdDigest(@NotNull String correlationId) throws NoSuchAlgorithmException {
        return Hex.encodeHexString(MessageDigest.getInstance("SHA-1").digest(correlationId.getBytes()));
    }

}
