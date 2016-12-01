package com.opuscapita.peppol.commons.errors;

import com.google.gson.JsonObject;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Daniil on 19.07.2016.
 *
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

    public void reportToServiceNow(String json, String customerId, Exception e) {
        reportToServiceNow(json, customerId, e, e.getMessage());
    }

    public void reportToServiceNow(String json, String customerId, Exception e, String shortDescription) {
        reportToServiceNow(json, customerId, e, shortDescription, "");
    }

    public void reportToServiceNow(String json, String customerId, Exception e, String shortDescription, String correlationIdPrefix) {
        String dumpFileName = storeMessageToDisk(json, e);
        logger.warn("Dumped erroneous message to: " + dumpFileName);
        createSncTicket(dumpFileName, json, customerId, e, shortDescription, correlationIdPrefix);
        logger.warn("ServiceNow ticket created with reference to: " + dumpFileName);
    }

    private void createSncTicket(String dumpFileName, String message, String customerId, Exception jse, String shortDescription, String correlationIdPrefix) {
        StringWriter stackTraceWriter = new StringWriter();
        jse.printStackTrace(new PrintWriter(stackTraceWriter));
        String correlationId = correlationIdPrefix + generateFailedMessageCorrelationId(jse);
        Yaml yaml = new Yaml();
        Object yamlifiedMessaged = yaml.load("\"" + message + "\"");
        try {
            serviceNowRest.insert(new SncEntity(shortDescription, yaml.dump(yamlifiedMessaged) + "\n\r" + dumpFileName + "\n\r" + stackTraceWriter.toString(),
                    correlationId, customerId, 0));
        } catch (IOException e) {
            logger.error("Unable to create SNC ticket", e);
        }
    }

    private String generateFailedMessageCorrelationId(Exception jse) {
        return jse.getClass().getName();
    }

    private String storeMessageToDisk(@NotNull String message, @Nullable Exception exp) {
        String messageDumpBaseFolderPath = getMessageDumpBaseFolderPath();
        String messageDumpFileName = generateMessageDumpFileName();
        File dumpFile = new File(messageDumpBaseFolderPath, messageDumpFileName);
        try (FileOutputStream fos = new FileOutputStream(dumpFile)) {
            fos.write((message + "\n\n").getBytes());
            if (exp != null) {
                fos.write(ExceptionUtils.getStackTrace(exp).getBytes());
            }
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
