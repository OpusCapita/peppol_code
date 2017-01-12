package com.opuscapita.peppol.commons.errors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Daniil on 19.07.2016.
 * <p>
 * TODO separate between our errors that should be handled by DTP and service desk related stuff
 */
@Component
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    private final ServiceNow serviceNowRest;
    private final Environment environment;

    @Autowired
    public ErrorHandler(@NotNull ServiceNow serviceNowRest, @NotNull Environment environment) {
        this.serviceNowRest = serviceNowRest;
        this.environment = environment;
    }

    public void reportToServiceNow(@NotNull ContainerMessage cm, @Nullable Exception e, @NotNull String shortDescription) {
        reportToServiceNow(cm.convertToJson(), cm.getCustomerId(), e, shortDescription);
    }

    public void reportToServiceNow(String json, String customerId, Exception e) {
        reportToServiceNow(json, customerId, e, e.getMessage());
    }

    public void reportToServiceNow(String json, String customerId, Exception e, String shortDescription) {
        reportToServiceNow(json, customerId, e, shortDescription, "");
    }

    public void reportToServiceNow(String json, String customerId, Exception e, String shortDescription, String correlationIdPrefix) {
        String dumpFileName = null;
        if (StringUtils.isNotBlank(json)) {
            dumpFileName = storeMessageToDisk(json);
            logger.info("Dumped erroneous message to: " + dumpFileName);
        }
        createSncTicket(dumpFileName, json, customerId, e, shortDescription, correlationIdPrefix);
    }

    private void createSncTicket(@Nullable String dumpFileName, @Nullable String json, @Nullable String customerId,
                                 @Nullable Exception jse, @NotNull String shortDescription, @Nullable String correlationIdPrefix) {
        String correlationId = correlationIdPrefix + generateFailedMessageCorrelationId(jse);
        String stackTrace = jse == null ? "" : ExceptionUtils.getStackTrace(jse);
        try {
            serviceNowRest.insert(
                    new SncEntity(
                            shortDescription,
                            dumpFileName == null ? stackTrace : makeMessageHumanReadable(json) + "\n" + dumpFileName + "\n" + stackTrace,
                            correlationId,
                            customerId,
                            0));
            logger.info("ServiceNow ticket created with reference to: " + dumpFileName);
        } catch (IOException e) {
            logger.error("Unable to create SNC ticket", e);
        }
    }

    /**
     * Make the message "great", errm... human readable again :)
     *
     * @param json the message
     * @return indented contents of json as a plain text or just a message itself, in any case { and } and " and ' are removed.
     */
    private String makeMessageHumanReadable(String json) {
        String detailedDescription;
        try {
            JsonObject jsonMessage = new JsonParser().parse(json).getAsJsonObject();
            detailedDescription = new GsonBuilder().setPrettyPrinting().create().toJson(jsonMessage);
        } catch (Exception e) {
            detailedDescription = json;
        }
        detailedDescription = detailedDescription.replaceAll("\\{|\\}|\\\"|\\'", "");
        return detailedDescription;
    }

    private String generateFailedMessageCorrelationId(@Nullable Exception jse) {
        return jse == null ? UUID.randomUUID().toString() : jse.getClass().getName();
    }

    private String storeMessageToDisk(@NotNull String message) {
        String messageDumpBaseFolderPath = getMessageDumpBaseFolderPath();
        String messageDumpFileName = generateMessageDumpFileName();
        File dumpFile = new File(messageDumpBaseFolderPath, messageDumpFileName);
        try (FileOutputStream fos = new FileOutputStream(dumpFile)) {
            fos.write((message + "\n\n").getBytes());
        } catch (IOException e) {
            logger.error("Failed to store message to disk ( " + dumpFile.getAbsolutePath() + " )", e);
            logger.error("Failed message: " + message);
        }
        return dumpFile.exists() ? dumpFile.getAbsolutePath() : "N/A";
    }

    private String getMessageDumpBaseFolderPath() {
        return environment.getProperty("peppol.error.handler.dump.directory", "/tmp/");
    }

    private String generateMessageDumpFileName() {
        String uniqueSuffix = RandomStringUtils.randomAlphanumeric(5);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss.SSS_z_");
        return dateFormat.format(new Date()) + uniqueSuffix + ".json";
    }

    public void reportFailureToAmqp(String message, Exception e, RabbitTemplate rabbitTemplate, String outgoingQueueName) {
        JsonObject errorDoc = new JsonObject();
        errorDoc.addProperty("error", e.getMessage());
        errorDoc.addProperty("message", message);
        errorDoc.addProperty("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend(outgoingQueueName, errorDoc.toString());
    }
}
