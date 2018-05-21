package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import eu.peppol.identifier.CustomizationIdentifier;
import eu.peppol.identifier.PeppolDocumentTypeId;
import org.jetbrains.annotations.NotNull;

/**
 * Moved from base document to a separate utility class.
 *
 * @author Sergejs.Roze
 */
public class OxalisUtils {

    public static PeppolDocumentTypeId getPeppolDocumentTypeId(@NotNull DocumentInfo document) {
        if(isSvefakturaOBjectEnvelope(document.getCustomizationId())) {
            return PeppolDocumentTypeId.valueOf("urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0");
        }
        return new PeppolDocumentTypeId(
                document.getRootNameSpace(),
                document.getRootNodeName(),
                new CustomizationIdentifier(document.getCustomizationId()),
                document.getVersionId()
        );

    }

    private static boolean isSvefakturaOBjectEnvelope(String customizationId) {
        return customizationId.contains("urn:sfti:services:documentprocessing:BasicInvoice:1:0###urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope");
    }

}
