package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

/**
 * @author Sergejs.Roze
 */
@PeppolDocument("SveFaktura1")
public class SveFaktura1Document extends BaseDocument {
    public static final String SBDH_ID = "urn:sfti:documents:BasicInvoice:1:0";

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

        return false;
    }
}
