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
    private Document document;
    private String fileName;

    @NotNull
    public Document getDocument() {
        return document;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    @NotNull
    public abstract String getSenderId();

    @NotNull
    public abstract String getRecipientId();

    public abstract boolean recognize(@NotNull Document document);

    @Nullable
    protected static String readSbdhStandard(Document document) {
        Node sbdh = XmlUtils.getSBDH(document);
        if (sbdh != null) {
            Node standard = XmlUtils.searchForXPath(sbdh, "DocumentIdentification", "Standard");
            if (standard != null) {
                return standard.getTextContent();
            }
        }
        return null;
    }

    @Nullable
    protected static String readDocumentProfileId(Document document) {
        Node root = XmlUtils.getRootNode(document);
        if (root != null) {
            Node profileId = XmlUtils.searchForXPath(root, "ProfileID");
            if (profileId != null) {
                return profileId.getTextContent();
            }
        }
        return null;
    }

    protected void setDocument(Document document) {
        this.document = document;
    }

    protected void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
