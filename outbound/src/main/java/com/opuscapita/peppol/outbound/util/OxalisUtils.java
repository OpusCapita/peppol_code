package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import no.difi.oxalis.sniffer.identifier.CustomizationIdentifier;
import no.difi.oxalis.sniffer.identifier.PeppolDocumentTypeId;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Moved from base document to a separate utility class.
 *
 * @author Sergejs.Roze
 */
public class OxalisUtils {

    public static DocumentTypeIdentifier getPeppolDocumentTypeId(@NotNull ContainerMessage cm) {
        PeppolDocumentTypeId peppolDocumentTypeId = createPeppolDocumentTypeId(cm.getDocumentInfo());
        if (StringUtils.isBlank(cm.getProcessingInfo().getSendingProtocol())) {
            return toVefa(peppolDocumentTypeId);
        }
        return toVefa(peppolDocumentTypeId, cm.getProcessingInfo().getSendingProtocol());
    }

    public static DocumentTypeIdentifier getPeppolDocumentTypeId(@NotNull DocumentInfo document) {
        PeppolDocumentTypeId peppolDocumentTypeId = createPeppolDocumentTypeId(document);
        return toVefa(peppolDocumentTypeId);
    }

    private static PeppolDocumentTypeId createPeppolDocumentTypeId(@NotNull DocumentInfo document) {
        PeppolDocumentTypeId peppolDocumentTypeId;
        if (isSvefakturaObjectEnvelope(document.getCustomizationId())) {
            peppolDocumentTypeId = new PeppolDocumentTypeId(
                    "urn:sfti:documents:StandardBusinessDocumentHeader",
                    "Invoice",
                    CustomizationIdentifier.valueOf("urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope"),
                    "1.0");
        } else {
            peppolDocumentTypeId = new PeppolDocumentTypeId(
                    document.getRootNameSpace(),
                    document.getRootNodeName(),
                    CustomizationIdentifier.valueOf(document.getCustomizationId()),
                    document.getVersionId()
            );
        }
        return peppolDocumentTypeId;
    }

    private static boolean isSvefakturaObjectEnvelope(String customizationId) {
        return customizationId.contains("urn:sfti:services:documentprocessing:BasicInvoice:1:0###" +
                "urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope");
    }

    private static DocumentTypeIdentifier toVefa(PeppolDocumentTypeId peppolDocumentTypeId) {
        return DocumentTypeIdentifier.of(peppolDocumentTypeId.toString());
    }

    private static DocumentTypeIdentifier toVefa(PeppolDocumentTypeId peppolDocumentTypeId, String scheme) {
        return DocumentTypeIdentifier.of(peppolDocumentTypeId.toString(), Scheme.of(scheme));
    }
}
