package com.opuscapita.peppol.support.ui.json;

import com.opuscapita.peppol.support.ui.domain.Message;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.5.12
 * Time: 11:58
 * To change this template use File | Settings | File Templates.
 */
public class MessageSerialization extends JsonSerializer<Message> {

    @Override
    public void serialize(Message message, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", message.getId());

        // Information about sender (removing nested objects for ngTable wrapping)
        jsonGenerator.writeNumberField("senderId", message.getSender().getId());
        jsonGenerator.writeStringField("senderIdentifier", message.getSender().getIdentifier());
        jsonGenerator.writeStringField("senderName", message.getSender().getName());

        // Message information
        jsonGenerator.writeStringField("recipientId", message.getRecipientId());
        jsonGenerator.writeStringField("invoiceNumber", message.getInvoiceNumber());
        jsonGenerator.writeStringField("invoiceDate", message.getInvoiceDate().toString());
        jsonGenerator.writeBooleanField("resolvedManually", message.isResolvedManually());
        jsonGenerator.writeStringField("resolvedComment", message.getResolvedComment());
        try {
            jsonGenerator.writeStringField("dueDate", message.getDueDate().toString());
        } catch (Exception e) {
            // Due date may be null
            jsonGenerator.writeStringField("dueDate", null);
        }

        // Check if files where initialized
        /*try {
            Set<FileInfo> fileInfoSet = message.getFiles();
            if (fileInfoSet.size() > 0) {
                jsonGenerator.writeArrayFieldStart("files");
                // File information
                for (FileInfo fileInfo : fileInfoSet) {
                    jsonGenerator.writeStartObject();
                    jsonGenerator.writeNumberField("id", fileInfo.getId());
                    jsonGenerator.writeStringField("filename", fileInfo.getFilename());
                    jsonGenerator.writeNumberField("fileSize", fileInfo.getFileSize());
                    jsonGenerator.writeStringField("arrivedTimeStamp", fileInfo.getArrivedTimeStamp().toString());
                    jsonGenerator.writeBooleanField("duplicate", fileInfo.isDuplicate());
                    // Lets check if failedInfo was initialized
                    try {
                        Set<FailedFileInfo> failedFileInfoSet = fileInfo.getFailedInfo();
                        if (failedFileInfoSet != null && failedFileInfoSet.size() > 0) {
                            jsonGenerator.writeArrayFieldStart("failed");
                            for (FailedFileInfo failedFileInfo : failedFileInfoSet) {
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField("ts", failedFileInfo.getTimestamp().toString());
                                jsonGenerator.writeStringField("errorMessage", failedFileInfo.getErrorMessage());
                                jsonGenerator.writeStringField("errorFilePath", failedFileInfo.getErrorFilePath());
                                jsonGenerator.writeBooleanField("invalid", failedFileInfo.isInvalid());
                                jsonGenerator.writeEndObject();
                            }
                            jsonGenerator.writeEndArray();
                        }
                    } catch (LazyInitializationException pass) {
                        // Ahh, nope, it wasn't :(
                    }
                    // Now lets check sentInfo
                    try {
                        Set<SentFileInfo> sentFileInfoSet = fileInfo.getSentInfo();
                        if (sentFileInfoSet != null && sentFileInfoSet.size() > 0) {
                            jsonGenerator.writeArrayFieldStart("sent");
                            for (SentFileInfo sentFileInfo : sentFileInfoSet) {
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField("ts", sentFileInfo.getTimestamp().toString());
                                jsonGenerator.writeBooleanField("forced", sentFileInfo.isForced());
                                jsonGenerator.writeEndObject();
                            }
                            jsonGenerator.writeEndArray();
                        }
                    } catch (LazyInitializationException pass) {
                        // and again not initialized...
                    }
                    // And lets finally check if it was reprocessed.
                    try {
                        Set<ReprocessFileInfo> reprocessFileInfoSet = fileInfo.getReprocessInfo();
                        if (reprocessFileInfoSet != null && reprocessFileInfoSet.size() > 0) {
                            jsonGenerator.writeArrayFieldStart("reprocessed");
                            for (ReprocessFileInfo reprocessFileInfo : reprocessFileInfoSet) {
                                jsonGenerator.writeStartObject();
                                jsonGenerator.writeStringField("ts", reprocessFileInfo.getTimestamp().toString());
                                jsonGenerator.writeEndObject();
                            }
                            jsonGenerator.writeEndArray();
                        }
                    } catch (LazyInitializationException pass) {
                        // Nothing  again :(
                    }
                    jsonGenerator.writeEndObject();
                }
                jsonGenerator.writeEndArray();
            }

        } catch (LazyInitializationException pass) {
            // Ignore
        }*/
        jsonGenerator.writeEndObject();
    }


}
