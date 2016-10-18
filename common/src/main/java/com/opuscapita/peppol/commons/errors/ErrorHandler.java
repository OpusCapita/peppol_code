package com.opuscapita.peppol.commons.errors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import org.apache.commons.lang3.RandomStringUtils;
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
 */
@Component
public class ErrorHandler {
    Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    @Autowired
    ServiceNow serviceNowRest;

    @Autowired
    Environment environment;

    @Autowired
    Gson gson;

    public void reportToServiceNow(String message, String customerId, Exception e) {
        reportToServiceNow(message, customerId, e, e.getMessage());
    }

    public void reportToServiceNow(String message, String customerId, Exception e, String shortDescription) {
        reportToServiceNow(message, customerId, e, shortDescription, "");
    }

    public void reportToServiceNow(String message, String customerId, Exception e, String shortDescription, String correlationIdPrefix) {
        String dumpFileName = storeMessageToDisk(message);
        logger.warn("Dumped erroneous message to: " + dumpFileName);
        createSncTicket(dumpFileName, message, customerId, e, shortDescription, correlationIdPrefix);
        logger.warn("ServiceNow ticket created with reference to: " + dumpFileName);
    }

    protected void createSncTicket(String dumpFileName, String message, String customerId, Exception jse, String shortDescription, String correlationIdPrefix) {
        StringWriter stackTraceWriter = new StringWriter();
        jse.printStackTrace(new PrintWriter(stackTraceWriter));
        String correlationId = correlationIdPrefix + generateFailedMessageCorrelationId(jse);
        Yaml yaml = new Yaml();
        Object yamlifiedMessaged = yaml.load(message);
        try {
            serviceNowRest.insert(new SncEntity(shortDescription, yaml.dump(yamlifiedMessaged) + "\n\r" + dumpFileName + "\n\r" + stackTraceWriter.toString(), correlationId, customerId, 0));
        } catch (IOException e) {
            logger.error("Unable to create SNC ticket", e);
        }
    }

    private String generateFailedMessageCorrelationId(Exception jse) {
        return jse.getClass().getName();
    }

    protected String storeMessageToDisk(String message) {
        String messageDumpBaseFolderPath = getMessageDumpBaseFolderPath();
        String messageDumpFileName = generateMessageDumpFileName();
        File dumpFile = new File(messageDumpBaseFolderPath, messageDumpFileName);
        try (FileOutputStream fos = new FileOutputStream(dumpFile)) {
            fos.write(message.getBytes());
        } catch (IOException e) {
            logger.error("Failed to store message to disk ( " + dumpFile.getAbsolutePath() + " )", e);
            logger.error("Failed message: " + message);
        }
        return dumpFile.exists() ? dumpFile.getAbsolutePath() : "N/A";
    }

    private String getMessageDumpBaseFolderPath() {
        return environment.getProperty("persistence.events.error.dumps.path", "/tmp/");
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
