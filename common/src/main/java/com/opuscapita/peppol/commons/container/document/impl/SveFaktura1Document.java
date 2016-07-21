package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import com.opuscapita.peppol.commons.container.document.impl.sf1.Svefaktura1FieldsReader;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.opuscapita.peppol.commons.container.document.DocumentUtils.readSbdhStandard;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("SimplifiableIfStatement")
@PeppolDocument("Svefaktura1 document")
public class SveFaktura1Document extends BaseDocument {
    private static final String SBDH_ID = "urn:sfti:documents:BasicInvoice:1:0";

    @Override
    public boolean fillFields() {
        Node rootNode = getRootNode();
        if (rootNode == null) {
            return false;
        }

        // SBDH is mandatory in Svefaktura1
        Node sbdhNode = getSbdhNode();
        if (sbdhNode == null) {
            return false;
        }

        return new Svefaktura1FieldsReader().fillFields(sbdhNode, rootNode, this);
    }

    @Override
    public boolean recognize(@NotNull Document document) {
        String fromSbdh = readSbdhStandard(document);
        if (fromSbdh != null) {
            return SBDH_ID.equals(fromSbdh);
        }

        return false;
    }
}
