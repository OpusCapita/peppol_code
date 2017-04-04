package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base document type for all known UBL documents: Invoice, Credit Note, whatever.
 *
 * @author Sergejs.Roze
 */
@PeppolDocument("UBL document")
public class UblDocument {
    protected static final String SBDH_ID = "urn:oasis:names:specification:ubl:schema:xsd:(Invoice|CreditNote)-2";
    protected static final String PROFILE_ID_MASK = "urn:www\\.cenbii\\.eu:profile:bii(04|05|xx):ver[1-2]\\.0";
    protected static final String EHF_CUSTOMIZATION = "extended:urn:www.difi.no:ehf";

    private static final Logger logger = LoggerFactory.getLogger(UblDocument.class);
    private final DocumentInfo documentInfo = new DocumentInfo();
    private UblDocumentType type;

//    @Override
//    public boolean fillFields() {
//        Node root = getRootNode();
//        if (root == null) {
//            logger.warn("Failed to find root node");
//            return false;
//        }
//
//        this.type = recognizeType(root.getLocalName());
//        if (type == UblDocumentType.UNDEFINED) {
//            logger.warn("Failed to recognize document subtype");
//            return false;
//        }
//
//        try {
//            FieldsReader reader = type.getReader().newInstance();
//            return reader.fillFields(getSbdhNode(), root, this);
//        } catch (Exception e) {
//            logger.error("Unable to read document fields", e);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean recognize(@NotNull Document document) {
//        String fromSbdh = DocumentUtils.readSbdhStandard(document);
//        if (fromSbdh != null) {
//            if (!fromSbdh.matches(SBDH_ID)) {
//                return false;
//            }
//        }
//
//        String docId = DocumentUtils.readDocumentProfileId(document);
//        if (docId != null) {
//            if (!docId.matches(PROFILE_ID_MASK)) {
//                return false;
//            }
//        }
//
//        String customizationId = DocumentUtils.readCustomizationId(document);
//        if (customizationId == null) {
//            return false;
//        }
//        if (customizationId.contains(EHF_CUSTOMIZATION)) {
//            return false;
//        }
//
//        Node root = DocumentUtils.getRootNode(document);
//        if (root == null) {
//            return false;
//        }
//
//        String rootName = root.getLocalName();
//        UblDocumentType type = recognizeType(rootName);
//
//        return type != UblDocumentType.UNDEFINED;
//    }
//
//    @NotNull
//    @Override
//    public Archetype getArchetype() {
//        return Archetype.PEPPOL_BIS;
//    }
//
//    @NotNull
//    @Override
//    public String getDocumentType() {
//        return type.getTag();
//    }
//
//    protected UblDocumentType recognizeType(@NotNull String rootName) {
//        for (int i = 0; i < UblDocumentType.values().length; i++) {
//            UblDocumentType current = UblDocumentType.values()[i];
//            String currentName = current.getTag();
//            if (currentName.equals(rootName)) {
//                return current;
//            }
//        }
//        return UblDocumentType.UNDEFINED;
//    }

}
