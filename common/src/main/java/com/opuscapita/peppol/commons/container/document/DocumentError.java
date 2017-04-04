package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * @author Sergejs.Roze
 */
public class DocumentError implements Serializable {
    private final Endpoint source;
    private final String message;

    public DocumentError(@NotNull Endpoint source, @NotNull String message) {
        this.source = source;
        this.message = message;
    }

    public Endpoint getSource() {
        return source;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return source + " error: " + message;
    }
}
