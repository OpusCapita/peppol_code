package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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

    public InvalidDocument(@NotNull String reason, @NotNull BaseDocument other) {
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

    @Override
    public String toString() {
        String result = "Processing of the document ";
        if (StringUtils.isNotBlank(getDocumentId())) {
            result += getDocumentId() + " ";
        }
        result += "failed: ";
        result += reason + "\n";

        if (StringUtils.isNotBlank(getIssueDate())) {
            result += "Issue date: " + getIssueDate() + "\n";
        }

        if (StringUtils.isNotBlank(getSenderId())) {
            result += "Sender ID: " + getSenderId() + "\n";
        }
        if (StringUtils.isNotBlank(getSenderName())) {
            result += "Sender name: " + getSenderName() + "\n";
        }

        if (StringUtils.isNotBlank(getRecipientId())) {
            result += "Recipient ID: " + getRecipientId() + "\n";
        }
        if (StringUtils.isNotBlank(getRecipientName())) {
            result += "Recipient name: " + getRecipientName() + "\n";
        }

        if (e != null) {
            result += "\n" + ExceptionUtils.getStackTrace(e) + "\n";
        }
        result += "Reference id: " + getFileName();
        return result;
    }
}
