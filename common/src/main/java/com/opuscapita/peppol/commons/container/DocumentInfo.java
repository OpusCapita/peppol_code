package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
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

    private final List<DocumentError> errors = new ArrayList<>();
    private final List<DocumentWarning> warnings = new ArrayList<>();
    private String senderId = UNKNOWN_SENDER;
    private String recipientId = UNKNOWN_RECIPIENT;
    private String versionId = UNKNOWN_VERSION_ID;
    private String documentId = UNKNOWN_DOCUMENT_ID;
    private String issueDate = UNKNOWN_ISSUE_DATE;
    private String dueDate = UNKNOWN_DUE_DATE;
    private String senderName = UNKNOWN_SENDER_NAME;
    private String senderCountryCode = UNKNOWN_SENDER_COUNTRY_CODE;
    private String recipientName = UNKNOWN_RECIPIENT_NAME;
    private String recipientCountryCode = UNKNOWN_RECIPIENT_COUNTRY_CODE;
    private String profileId = UNKNOWN_PROFILE_ID;
    private String customizationId = UNKNOWN_CUSTOMIZATION_ID;
    private String rootNameSpace;
    private String rootNodeName;
    private Archetype archetype = Archetype.INVALID;
    private String documentType = UNKNOWN_DOCUMENT_TYPE;

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
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    @NotNull
    public String getCustomizationId() {
        return customizationId;
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
}
