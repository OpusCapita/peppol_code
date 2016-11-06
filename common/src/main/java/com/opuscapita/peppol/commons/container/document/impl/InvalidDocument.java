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

    public InvalidDocument(@NotNull String reason, @Nullable Exception e) {
        this.reason = reason;
        this.e = e;
    }

    public InvalidDocument(@NotNull String reason) {
        this(reason, null);
    }

    public InvalidDocument(@Nullable BaseDocument original, @NotNull String reason, @Nullable Exception e) {
        if (original != null) {
            setCustomizationId(original.getCustomizationId());
            setProfileId(original.getProfileId());
            setVersionId(original.getVersionId());

            setDocumentId(original.getDocumentId());
            setIssueDate(original.getIssueDate());
            setDueDate(original.getDueDate());

            setSenderId(original.getSenderId());
            setSenderName(original.getSenderName());
            setSenderCountryCode(original.getSenderCountryCode());

            setRecipientId(original.getRecipientId());
            setRecipientName(original.getRecipientName());
            setRecipientCountryCode(original.getRecipientCountryCode());
        }

        this.reason = reason;
        this.e = e;
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
        return result;
    }
}
