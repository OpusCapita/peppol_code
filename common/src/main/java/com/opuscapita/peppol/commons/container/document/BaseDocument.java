package com.opuscapita.peppol.commons.container.document;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Main abstract class for all documents even invalid ones.
 *
 * @author Sergejs.Roze
 */
public abstract class BaseDocument {
    public static final String UNKNOWN_RECIPIENT = "n/a";
    public static final String UNKNOWN_SENDER = "n/a";
    public static final String UNKNOWN_VERSION_ID = "Unknown version id";
    public static final String UNKNOWN_DOCUMENT_ID = "";
    public static final String UNKNOWN_ISSUE_DATE = "";
    public static final String UNKNOWN_DUE_DATE = "";
    public static final String UNKNOWN_SENDER_NAME = "n/a";
    public static final String UNKNOWN_SENDER_COUNTRY_CODE = "";
    public static final String UNKNOWN_RECIPIENT_NAME = "n/a";
    public static final String UNKNOWN_RECIPIENT_COUNTRY_CODE = "";
    public static final String UNKNOWN_PROFILE_ID = "Unknown profile ID";
    public static final String UNKNOWN_CUSTOMIZATION_ID = "Unknown customization ID";

    protected String senderId = UNKNOWN_SENDER;
    protected String recipientId = UNKNOWN_RECIPIENT;
    protected String versionId = UNKNOWN_VERSION_ID;
    protected String documentId = UNKNOWN_DOCUMENT_ID;
    protected String issueDate = UNKNOWN_ISSUE_DATE;
    protected String dueDate = UNKNOWN_DUE_DATE;
    protected String senderName = UNKNOWN_SENDER_NAME;
    protected String senderCountryCode = UNKNOWN_SENDER_COUNTRY_CODE;
    protected String recipientName = UNKNOWN_RECIPIENT_NAME;
    protected String recipientCountryCode = UNKNOWN_RECIPIENT_COUNTRY_CODE;
    protected String profileId = UNKNOWN_PROFILE_ID;
    protected String customizationId = UNKNOWN_CUSTOMIZATION_ID;

    private Document document;
    private String fileName;

    private Node root;
    private Node sbdh;

    @Nullable
    public Node getRootNode() {
        if (root == null) {
            root = DocumentUtils.getRootNode(getDocument());
        }
        return root;
    }

    @Nullable
    public Node getSbdhNode() {
        if (sbdh == null) {
            sbdh = DocumentUtils.getSBDH(getDocument());
        }
        return sbdh;
    }

    @NotNull
    public Document getDocument() {
        return document;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    /**
     * This method must fill all possible fields (see above).
     *
     * @return true when all possible fields are successfully read, otherwise false
     */
    public abstract boolean fillFields();

    /**
     * Must return true if the incoming document is exactly of our type.
     * Please be careful and don't return true on other document types.
     *
     * @param document the document in question. The document field is not set in object instance
     * @return true when the document is of exactly this type, otherwise false
     */
    public abstract boolean recognize(@NotNull Document document);

    @NotNull
    public final String getSenderId() {
        return senderId;
    }

    @NotNull
    public final String getRecipientId() {
        return recipientId;
    }

    @NotNull
    public String getVersionId() {
        return versionId;
    }

    @NotNull
    public String getDocumentId() {
        return documentId;
    }

    @NotNull
    public String getIssueDate() {
        return issueDate;
    }

    @NotNull
    public String getDueDate() {
        return dueDate;
    }

    @NotNull
    public String getSenderName() {
        return senderName;
    }

    @NotNull
    public String getSenderCountryCode() {
        return senderCountryCode;
    }

    @NotNull
    public String getRecipientName() {
        return recipientName;
    }

    @NotNull
    public String getRecipientCountryCode() {
        return recipientCountryCode;
    }

    @NotNull
    public String getProfileId() {
        return profileId;
    }

    @NotNull
    public String getCustomizationId() {
        return customizationId;
    }

    protected void setDocument(Document document) {
        this.document = document;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setSenderCountryCode(String senderCountryCode) {
        this.senderCountryCode = senderCountryCode;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipientCountryCode(String recipientCountryCode) {
        this.recipientCountryCode = recipientCountryCode;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public void setCustomizationId(String customizationId) {
        this.customizationId = customizationId;
    }
}
