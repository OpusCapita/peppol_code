package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

/**
 * @author Sergejs.Roze
 */
@PeppolDocument("UBL document")
public class UblDocument extends BaseDocument {
    public static final String SBDH_ID = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2";
    public static final String PROFILE_ID_MASK = "urn:www\\.cenbii\\.eu:profile:bii0[4-5]:ver[1-2]\\.0";

    @NotNull
    @Override
    public String getSenderId() {
        return null;
    }

    @NotNull
    @Override
    public String getRecipientId() {
        return null;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean recognize(@NotNull Document document) {
        String fromSbdh = readSbdhStandard(document);
        if (fromSbdh != null) {
            return SBDH_ID.equals(fromSbdh);
        }

        String docId = readDocumentProfileId(document);
        if (docId != null) {
            return docId.matches(PROFILE_ID_MASK);
        }

        return false;
    }
}
