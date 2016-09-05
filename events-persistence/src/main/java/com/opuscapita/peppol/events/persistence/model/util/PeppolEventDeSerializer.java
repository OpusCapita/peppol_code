package com.opuscapita.peppol.events.persistence.model.util;

import com.google.gson.*;
import com.opuscapita.peppol.events.persistence.model.PeppolEvent;
import com.opuscapita.peppol.events.persistence.model.TransportType;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by bambr on 16.5.9.
 */
public class PeppolEventDeSerializer implements JsonDeserializer<PeppolEvent> {

    @Override
    public PeppolEvent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        PeppolEvent result = new PeppolEvent();
        populatePeppolEvent(jsonElement, result);
        return result;
    }

    protected void populatePeppolEvent(JsonElement jsonElement, PeppolEvent result) {
        JsonObject rawJson = jsonElement.getAsJsonObject();
        Map<String, Boolean> stringFields = new TreeMap<String, Boolean>() {{
            put("commonName", false);
            put("documentType", false);
            put("dueDate", false);
            put("errorFilePath", false);
            put("errorMessage", false);
            put("fileName", true);
            put("invoiceDate", false);
            put("invoiceId", false);
            put("documentType", false);
            put("recipientCountryCode", false);
            put("recipientId", false);
            put("recipientName", false);
            put("senderCountryCode", false);
            put("senderId", false);
            put("senderName", false);
            put("sendingProtocol", false);
            put("transactionId", false);
        }};
        stringFields.entrySet().forEach(field -> populateFieldStringValue(result, rawJson, field.getKey(), field.getValue()));

        populateFieldTransportTypeValue(result, rawJson, "transportType", true);

        populateFieldLongValue(result, rawJson, "fileSize", true);
    }

    private void populateFieldLongValue(PeppolEvent result, JsonObject rawJson, String fieldName, Boolean mandatory) {
        if (canPopulateField(rawJson, fieldName, mandatory)) {
            result.setFileSize(rawJson.get(fieldName).getAsLong());
        }
    }

    private void populateFieldTransportTypeValue(PeppolEvent result, JsonObject rawJson, String fieldName, Boolean mandatory) {
        if (canPopulateField(rawJson, fieldName, mandatory)) {
            result.setTransportType(TransportType.valueOf(rawJson.get(fieldName).getAsString()));
        }
    }

    private void populateFieldStringValue(PeppolEvent result, JsonObject rawJson, String fieldName, Boolean mandatory) {
        if (canPopulateField(rawJson, fieldName, mandatory)) {
            result.setCommonName(rawJson.get(fieldName).getAsString());
        }
    }

    private boolean canPopulateField(JsonObject rawJson, String fieldName, Boolean mandatory) {
        return mandatory && rawJson.has(fieldName);
    }
}
