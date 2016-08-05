package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;

/**
 * Used for files that cannot be loaded, parsed or recognized.
 *
 * @author Sergejs.Roze
 */
public class InvalidDocument extends BaseDocument {
    private final String reason;
    private final Exception e;

    public InvalidDocument(@NotNull String reason, @Nullable Exception e, @NotNull String fileName) {
        this.reason = reason;
        this.e = e;
        setFileName(fileName);
    }

    public InvalidDocument(String reason, BaseDocument other) {
        this.reason = reason;
        this.e = null;
        setFileName(other.getFileName());
    }

    @Override
    public boolean fillFields() {
        return false;
    }

    @Override
    public boolean recognize(@NotNull Document document) {
        return false;
    }

    @NotNull
    @Override
    public Archetype getArchetype() {
        return Archetype.INVALID;
    }

    public String getReason() {
        return reason;
    }

    public Exception getException() {
        return e;
    }
}
