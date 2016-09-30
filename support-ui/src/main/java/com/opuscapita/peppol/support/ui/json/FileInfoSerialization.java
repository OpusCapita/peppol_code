package com.opuscapita.peppol.support.ui.json;

import com.opuscapita.peppol.support.ui.domain.FailedFileInfo;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.ReprocessFileInfo;
import com.opuscapita.peppol.support.ui.domain.SentFileInfo;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.hibernate.LazyInitializationException;

import java.io.IOException;
import java.util.Set;

/**
 * Created by KACENAR1 on 2014.04.29..
 */
public class FileInfoSerialization extends JsonSerializer<FileInfo> {
    @Override
    public void serialize(FileInfo fileInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        // File information
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
                    jsonGenerator.writeStringField("transmissionId", sentFileInfo.getTransmissionId());
                    jsonGenerator.writeStringField("apId", sentFileInfo.getApId());
                    jsonGenerator.writeStringField("apCompanyName", sentFileInfo.getApCompanyName());
                    jsonGenerator.writeStringField("apProtocol", sentFileInfo.getApProtocol());
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
}
