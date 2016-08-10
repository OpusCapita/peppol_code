package com.opuscapita.peppol.commons.errors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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
        String dumpFileName = storeMessageToDisk(message);
        createSncTicket(dumpFileName, customerId, e);
    }

    protected void createSncTicket(String dumpFileName, String customerId, Exception jse) {
        StringWriter stackTraceWriter = new StringWriter();
        jse.printStackTrace(new PrintWriter(stackTraceWriter));
        String correlationId = generateFailedMessageCorrelationId(jse);
        try {
            serviceNowRest.insert(new SncEntity(jse.getMessage(), dumpFileName + "\n\r" + stackTraceWriter.toString(), correlationId, customerId, 0));
        } catch (IOException e) {
            logger.error("Unable to create SNC ticket", e);
        }
    }

    private String generateFailedMessageCorrelationId(Exception jse) {
        return jse.getCause().getClass().getName();
    }

    protected String storeMessageToDisk(String message) {
        String messageDumpBaseFolderPath = getMessageDumpBaseFolderPath();
        String messageDumpFileName = generateMessageDumpFileName();
        File dumpFile = new File(messageDumpBaseFolderPath, messageDumpFileName);
        try (FileOutputStream fos = new FileOutputStream(dumpFile)) {
            fos.write(message.getBytes());
        } catch (IOException e) {
            logger.error("failed to store message to disk", e);
        }
        return dumpFile.exists() ? dumpFile.getAbsolutePath() : "N/A";
    }

    private String getMessageDumpBaseFolderPath() {
        return environment.getProperty("persistence.events.error.files.path", "/tmp/");
    }

    private String generateMessageDumpFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_z");
        return dateFormat.format(new Date()) + ".json";
    }

    public void reportFailureToAmqp(String message, Exception e, RabbitTemplate rabbitTemplate, String outgoingQueueName) {
        JsonObject errorDoc = new JsonObject();
        errorDoc.addProperty("error", e.getMessage());
        errorDoc.addProperty("message", message);
        errorDoc.addProperty("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend(outgoingQueueName, errorDoc.toString());
    }
}
