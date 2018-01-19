package com.opuscapita.peppol.commons.container;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Main abstract class for all documents even invalid ones. Holds main document data used during processing.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class DocumentInfo implements Serializable {
    private static final long serialVersionUID = 8462853143385799024L;

    public static final String UNKNOWN_RECIPIENT = "";
    public static final String UNKNOWN_SENDER = "";
    public static final String UNKNOWN_VERSION_ID = "";
    public static final String UNKNOWN_DOCUMENT_ID = "";
    public static final String UNKNOWN_ISSUE_DATE = "";
    public static final String UNKNOWN_DUE_DATE = "";
    public static final String UNKNOWN_SENDER_NAME = "";
    public static final String UNKNOWN_SENDER_COUNTRY_CODE = "";
    public static final String UNKNOWN_RECIPIENT_NAME = "";
    public static final String UNKNOWN_RECIPIENT_COUNTRY_CODE = "";
    public static final String UNKNOWN_PROFILE_ID = "";
    public static final String UNKNOWN_CUSTOMIZATION_ID = "";
    public static final String UNKNOWN_DOCUMENT_TYPE = "";
    public static final String UNKNOWN_ISSUE_TIME = "";
    public static final String UNKNOWN_BUSINESS_IDENTIFIER = "";

    @Since(1.0)
    private final List<DocumentError> errors = new ArrayList<>();
    @Since(1.0)
    private final List<DocumentWarning> warnings = new ArrayList<>();
    @Since(1.0)
    private String senderId = UNKNOWN_SENDER;
    @Since(1.0)
    private String recipientId = UNKNOWN_RECIPIENT;
    @Since(1.0)
    private String versionId = UNKNOWN_VERSION_ID;
    @Since(1.0)
    private String documentId = UNKNOWN_DOCUMENT_ID;
    @Since(1.0)
    private String issueDate = UNKNOWN_ISSUE_DATE;
    @Since(1.0)
    private String issueTime = UNKNOWN_ISSUE_TIME;
    @Since(1.0)
    private String dueDate = UNKNOWN_DUE_DATE;
    @Since(1.0)
    private String senderName = UNKNOWN_SENDER_NAME;
    @Since(1.0)
    private String senderCountryCode = UNKNOWN_SENDER_COUNTRY_CODE;
    @Since(1.0)
    private String recipientName = UNKNOWN_RECIPIENT_NAME;
    @Since(1.0)
    private String recipientCountryCode = UNKNOWN_RECIPIENT_COUNTRY_CODE;
    @Since(1.0)
    private String profileId = UNKNOWN_PROFILE_ID;
    @Since(1.0)
    private String customizationId = UNKNOWN_CUSTOMIZATION_ID;
    @Since(1.0)
    private String rootNameSpace;
    @Since(1.0)
    private String rootNodeName;
    @Since(1.0)
    private Archetype archetype = Archetype.INVALID;
    @Since(1.0)
    private String documentType = UNKNOWN_DOCUMENT_TYPE;
    @Since(1.0)
    private String documentBusinessIdentifier = UNKNOWN_BUSINESS_IDENTIFIER;

    public void with(@NotNull String key, @NotNull String value) {
        switch (key) {
            case "sender_id": {
                setSenderId(value);
                break;
            }
            case "recipient_id": {
                setRecipientId(value);
                break;
            }
            case "version_id": {
                setVersionId(value);
                break;
            }
            case "document_id": {
                setDocumentId(value);
                break;
            }
            case "issue_date": {
                setIssueDate(value);
                break;
            }
            case "issue_time": {
                setIssueTime(value);
                break;
            }
            case "due_date": {
                setDueDate(value);
                break;
            }
            case "sender_name": {
                setSenderName(value);
                break;
            }
            case "sender_country_code": {
                setSenderCountryCode(value);
                break;
            }
            case "recipient_name": {
                setRecipientName(value);
                break;
            }
            case "recipient_country_code": {
                setRecipientCountryCode(value);
                break;
            }
            case "profile_id": {
                setProfileId(value);
                break;
            }
            case "customization_id": {
                setCustomizationId(value);
                break;
            }
            case "document_business_identifier": {
                setDocumentBusinessIdentifier(value);
                break;
            }
        }
    }

    @NotNull
    public final String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @NotNull
    public final String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    @NotNull
    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @NotNull
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @NotNull
    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    @NotNull
    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @NotNull
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @NotNull
    public String getSenderCountryCode() {
        return senderCountryCode;
    }

    public void setSenderCountryCode(String senderCountryCode) {
        this.senderCountryCode = senderCountryCode;
    }

    @NotNull
    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    @NotNull
    public String getRecipientCountryCode() {
        return recipientCountryCode;
    }

    public void setRecipientCountryCode(String recipientCountryCode) {
        this.recipientCountryCode = recipientCountryCode;
    }

    @NotNull
    public String getProfileId() {
        return profileId.trim();
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @NotNull
    public String getCustomizationId() {
        return customizationId.trim();
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }

    @NotNull
    public Archetype getArchetype() {
        return archetype;
    }

    public void setArchetype(@NotNull Archetype archetype) {
        this.archetype = archetype;
    }

    @NotNull
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(@Nullable String documentType) {
        this.documentType = documentType;
    }

    @NotNull
    public List<DocumentError> getErrors() {
        return errors;
    }

    @NotNull
    public List<DocumentWarning> getWarnings() {
        return warnings;
    }

    @Nullable
    public String getRootNameSpace() {
        return rootNameSpace;
    }

    public void setRootNameSpace(@Nullable String rootNameSpace) {
        this.rootNameSpace = rootNameSpace;
    }

    @Nullable
    public String getRootNodeName() {
        return rootNodeName;
    }

    public void setRootNodeName(@Nullable String rootNodeName) {
        this.rootNodeName = rootNodeName;
    }

    @NotNull
    public String getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(@NotNull String issueTime) {
        this.issueTime = issueTime;
    }

    @NotNull
    public String getDocumentBusinessIdentifier() {
        if (StringUtils.isBlank(documentBusinessIdentifier)) {
            return documentId;
        }
        return documentBusinessIdentifier;
    }

    public void setDocumentBusinessIdentifier(@NotNull String documentBusinessIdentifier) {
        this.documentBusinessIdentifier = documentBusinessIdentifier;
    }
}
