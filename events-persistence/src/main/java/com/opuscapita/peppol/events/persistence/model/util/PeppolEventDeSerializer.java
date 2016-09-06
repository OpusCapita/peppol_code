package com.opuscapita.peppol.events.persistence.model.util;

import com.google.gson.*;
import com.opuscapita.peppol.events.persistence.model.PeppolEvent;

import java.lang.reflect.Type;

/**
 * Created by bambr on 16.5.9.
 */
public class PeppolEventDeSerializer implements JsonDeserializer<PeppolEvent> {

    @Override
    public PeppolEvent deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        PeppolEvent result = fixSenderName(jsonElement);
        return result;
    }

    protected PeppolEvent fixSenderName(JsonElement jsonElement) {
        JsonObject rawObject = jsonElement.getAsJsonObject();
        if (!rawObject.has("senderName")) {
            rawObject.addProperty("senderName", "n/a");
        }
        return new Gson().fromJson(rawObject, PeppolEvent.class);
    }
}
