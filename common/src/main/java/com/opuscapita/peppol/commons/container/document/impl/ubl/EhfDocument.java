package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.container.document.PeppolDocument;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.container.document.impl.UblDocument;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Sergejs.Roze
 */
@PeppolDocument("EHF document")
public class EhfDocument extends UblDocument {

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

        String customizationId = DocumentUtils.readCustomizationId(document);
        if (customizationId == null) {
            return false;
        }
        if (!customizationId.contains(EHF_CUSTOMIZATION)) {
            return false;
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
        return Archetype.EHF;
    }

}
