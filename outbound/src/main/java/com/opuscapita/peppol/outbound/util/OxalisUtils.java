package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import no.difi.oxalis.sniffer.identifier.CustomizationIdentifier;
import no.difi.oxalis.sniffer.identifier.PeppolDocumentTypeId;
import no.difi.vefa.peppol.common.lang.PeppolParsingException;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import org.jetbrains.annotations.NotNull;

/**
 * Moved from base document to a separate utility class.
 *
 * @author Sergejs.Roze
 */
public class OxalisUtils {

    public static DocumentTypeIdentifier getPeppolDocumentTypeId(@NotNull DocumentInfo document) throws PeppolParsingException {
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
        return toVefa(peppolDocumentTypeId);

    }

    private static boolean isSvefakturaObjectEnvelope(String customizationId) {
        return customizationId.contains("urn:sfti:services:documentprocessing:BasicInvoice:1:0###" +
                "urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope");
    }


    private static DocumentTypeIdentifier toVefa(PeppolDocumentTypeId peppolDocumentTypeId) throws PeppolParsingException {
        return DocumentTypeIdentifier.parse(peppolDocumentTypeId.toString());
    }
}
