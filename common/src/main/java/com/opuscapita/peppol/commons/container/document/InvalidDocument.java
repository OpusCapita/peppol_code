package com.opuscapita.peppol.commons.container.document;

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
    private final String fileName;

    public InvalidDocument(@NotNull String reason, @Nullable Exception e, @NotNull String fileName) {
        this.reason = reason;
        this.e = e;
        this.fileName = fileName;
    }

    @Override
    public String getSenderId() {
        return null;
    }

    @Override
    public String getRecipientId() {
        return null;
    }

    @Override
    public boolean recognize(@NotNull Document document) {
        return false;
    }
}
