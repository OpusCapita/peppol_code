package com.opuscapita.peppol.commons.container.document;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Gson fails on DOM Document serialization starting endless loop.
 * To prevent this we're not storing DOM Document and store the file name and read the file instead.
 *
 * @author Sergejs.Roze
 */
public class GsonDocumentAdapter extends TypeAdapter<BaseDocument> {
    private String fileName;

    @SuppressWarnings("unused")
    public GsonDocumentAdapter() {}

    public GsonDocumentAdapter(@Nullable String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void write(JsonWriter jsonWriter, BaseDocument document) throws IOException {
        if (document == null || fileName == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(fileName);
        }
    }

    @Override
    @Nullable
    public BaseDocument read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        fileName = jsonReader.nextString();
        return new DocumentLoader().load(fileName);
    }
}
