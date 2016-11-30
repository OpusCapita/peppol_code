package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import com.opuscapita.peppol.commons.container.document.impl.ubl.UblDocumentType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Base document type for all known UBL documents: Invoice, Credit Note, whatever.
 *
 * @author Sergejs.Roze
 */
@PeppolDocument("UBL document")
public class UblDocument extends BaseDocument {
    private static final Logger logger = LoggerFactory.getLogger(UblDocument.class);

    private static final String SBDH_ID = "urn:oasis:names:specification:ubl:schema:xsd:(Invoice|CreditNote)-2";
    private static final String PROFILE_ID_MASK = "urn:www\\.cenbii\\.eu:profile:bii(04|05|xx):ver[1-2]\\.0";

    private UblDocumentType type;

    @Override
    public boolean fillFields() {
        Node root = getRootNode();
        if (root == null) {
            return false;
        }

        this.type = recognizeType(root.getLocalName());
        if (type == UblDocumentType.UNDEFINED) {
            return false;
        }

        try {
            FieldsReader reader = type.getReader().newInstance();
            return reader.fillFields(getSbdhNode(), root, this);
        } catch (Exception e) {
            logger.error("Unable to read document fields", e);
            return false;
        }
    }

    @Override
    public boolean recognize(@NotNull Document document) {
        String fromSbdh = DocumentUtils.readSbdhStandard(document);
        if (fromSbdh != null) {
            if (!fromSbdh.matches(SBDH_ID)) {
                return false;
            }
        }

        String docId = DocumentUtils.readDocumentProfileId(document);
        if (docId != null) {
            if (!docId.matches(PROFILE_ID_MASK)) {
                return false;
            }
        }

        Node root = DocumentUtils.getRootNode(document);
        if (root == null) {
            return false;
        }

        String rootName = root.getLocalName();
        UblDocumentType type = recognizeType(rootName);

        return type != UblDocumentType.UNDEFINED;
    }

    @NotNull
    @Override
    public Archetype getArchetype() {
        return Archetype.UBL;
    }

    @NotNull
    @Override
    public String getDocumentType() {
        return type.getTag();
    }

    private UblDocumentType recognizeType(String rootName) {
        for (int i = 0; i < UblDocumentType.values().length; i++) {
            UblDocumentType current = UblDocumentType.values()[i];
            String currentName = current.getTag();
            if (currentName.equals(rootName)) {
                return current;
            }
        }
        return UblDocumentType.UNDEFINED;
    }

}
