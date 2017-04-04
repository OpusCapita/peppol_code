package com.opuscapita.peppol.commons.container.document.impl;

import com.opuscapita.peppol.commons.container.document.PeppolDocument;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("SimplifiableIfStatement")
@PeppolDocument("Svefaktura1 document")
public class SveFaktura1Document {
    private static final String SBDH_ID = "urn:sfti:documents:BasicInvoice:1:0";

//    public boolean fillFields() {
//        Node rootNode = getRootNode();
//        if (rootNode == null) {
//            return false;
//        }
//
//        // SBDH is mandatory in Svefaktura1
//        Node sbdhNode = getSbdhNode();
//        if (sbdhNode == null) {
//            return false;
//        }
//
//        return new Svefaktura1().fillFields(sbdhNode, rootNode, this);
//    }
//
//    @Override
//    public boolean recognize(@NotNull Document document) {
//        String fromSbdh = readSbdhStandard(document);
//        if (fromSbdh != null) {
//            return SBDH_ID.equals(fromSbdh);
//        }
//
//        return false;
//    }
//
//    @NotNull
//    @Override
//    public Archetype getArchetype() {
//        return Archetype.SVEFAKTURA1;
//    }
//
//    @NotNull
//    @Override
//    public String getDocumentType() {
//        return getRootNode() == null ? "NA" : getRootNode().getLocalName();
//    }
}
