package com.opuscapita.peppol.commons.errors;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
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
 */
@Component
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    private final ServiceNow serviceNowRest;
    private final ContainerMessageSerializer serializer;

    @Autowired
    public ErrorHandler(@NotNull ServiceNow serviceNowRest, @NotNull ContainerMessageSerializer serializer) {
        this.serviceNowRest = serviceNowRest;
        this.serializer = serializer;
    }

    public void reportWithContainerMessage(@NotNull ContainerMessage cm, @Nullable Throwable e, @NotNull String shortDescription) {
        reportWithContainerMessage(cm, e, shortDescription, null);
    }

    public void reportWithContainerMessage(@NotNull ContainerMessage cm, @Nullable Throwable e, @NotNull String shortDescription,
                                           @Nullable String additionalDetails) {
        createTicketFromContainerMessage(cm, e, shortDescription, additionalDetails);
    }

    public void reportWithoutContainerMessage(@Nullable String customerId, @Nullable Throwable e, @NotNull String shortDescription,
                                              @Nullable String correlationId, @Nullable String fileName) {
        reportWithoutContainerMessage(customerId, e, shortDescription, correlationId, fileName, null);
    }

    public void reportWithoutContainerMessage(@Nullable String customerId, @Nullable Throwable e, @NotNull String shortDescription,
                                              @Nullable String correlationId, @Nullable String fileName, @Nullable String additionalDetails) {
        createTicketWithoutContainerMessage(customerId, e, fileName, shortDescription, correlationId, additionalDetails);
    }

    private void createTicketWithoutContainerMessage(@Nullable String customerId, @Nullable Throwable e, @Nullable String fileName,
                                                     @NotNull String shortDescription, @Nullable String correlationId, String additionalDetails) {
        String detailedDescription =
                ErrorFormatter.getErrorDescription(customerId, e, fileName, additionalDetails);

        if (fileName == null) {
            fileName = "n/a";
        }

        String exceptionMessage = ErrorFormatter.exceptionMessageToString(e);

        if (StringUtils.isBlank(correlationId)) {
            correlationId = fileName;
            if (StringUtils.isNotBlank(exceptionMessage)) {
                correlationId += exceptionMessage;
            }
        }

        try {
            correlationId = correlationIdDigest(correlationId);
        } catch (NoSuchAlgorithmException e1) {
            logger.error("Failed to create SHA-1 has of correlation id");
            e1.printStackTrace();
        }
        createTicket(shortDescription, detailedDescription, correlationId, customerId, fileName);
    }

    private void createTicketFromContainerMessage(@NotNull ContainerMessage cm, @Nullable Throwable e,
                                                  @NotNull String shortDescription, @Nullable String additionalDetails) {
        String detailedDescription = ErrorFormatter.getErrorDescription(cm, e, additionalDetails, serializer);

        createTicket(shortDescription, detailedDescription, cm.getCorrelationId() + cm.getProcessingInfo().getCurrentStatus(),
                cm.getCustomerId(), cm.getFileName());
    }

    private void createTicket(@NotNull String shortDescription, @NotNull String detailedDescription,
                              @NotNull String correlationId, @Nullable String customerId, @NotNull String fileName) {
        if (StringUtils.isBlank(customerId)) {
            customerId = "n/a";
        }
        try {
            String md5 = correlationIdDigest(correlationId);
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