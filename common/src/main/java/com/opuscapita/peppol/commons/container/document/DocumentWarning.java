package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
public class DocumentWarning {
    private final Endpoint source;
    private final String message;

    public DocumentWarning(@NotNull Endpoint source, @NotNull String message) {
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
        return source + " warning: " + message;
    }

}
