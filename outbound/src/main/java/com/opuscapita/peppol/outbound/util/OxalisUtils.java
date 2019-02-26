package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import no.difi.oxalis.sniffer.identifier.CustomizationIdentifier;
import no.difi.oxalis.sniffer.identifier.PeppolDocumentTypeId;
import no.difi.vefa.peppol.common.model.DocumentTypeIdentifier;
import no.difi.vefa.peppol.common.model.ProcessIdentifier;
import no.difi.vefa.peppol.common.model.Scheme;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Moved from base document to a separate utility class.
 *
 * @author Sergejs.Roze
 */
public class OxalisUtils {

    public static ProcessIdentifier getPeppolProcessTypeId(@NotNull ContainerMessage cm, Scheme scheme) {
        PeppolMessageMetadata metadata = cm.getProcessingInfo().getPeppolMessageMetadata();
        if (metadata != null) {
            if (StringUtils.isNotBlank(metadata.getProfileTypeIdentifier())) {
                return ProcessIdentifier.of(metadata.getProfileTypeIdentifier(), scheme);
            }
        }
        return ProcessIdentifier.of(cm.getDocumentInfo().getProfileId(), scheme);
    }

    public static DocumentTypeIdentifier getPeppolDocumentTypeId(@NotNull ContainerMessage cm) {
        PeppolMessageMetadata metadata = cm.getProcessingInfo().getPeppolMessageMetadata();
        if (metadata != null) {
            if (StringUtils.isNotBlank(metadata.getDocumentTypeIdentifier())) {
                if (StringUtils.isNotBlank(metadata.getProtocol())) {
                    return DocumentTypeIdentifier.of(metadata.getDocumentTypeIdentifier(), Scheme.of(metadata.getProtocol()));
                }
                return DocumentTypeIdentifier.of(metadata.getDocumentTypeIdentifier());
            }

            String documentTypeIdentifier = createDocumentTypeIdentifierFromPayload(cm.getDocumentInfo());
            if (StringUtils.isNotBlank(metadata.getProtocol())) {
                return DocumentTypeIdentifier.of(documentTypeIdentifier, Scheme.of(metadata.getProtocol()));
            }
            return DocumentTypeIdentifier.of(documentTypeIdentifier);
        }
        return getPeppolDocumentTypeId(cm.getDocumentInfo());
    }

    public static DocumentTypeIdentifier getPeppolDocumentTypeId(@NotNull DocumentInfo document) {
        String documentTypeIdentifier = createDocumentTypeIdentifierFromPayload(document);
        return DocumentTypeIdentifier.of(documentTypeIdentifier);
    }

    private static String createDocumentTypeIdentifierFromPayload(@NotNull DocumentInfo document) {
        if (isSvefakturaObjectEnvelope(document.getCustomizationId())) {
            return new PeppolDocumentTypeId(
                    "urn:sfti:documents:StandardBusinessDocumentHeader",
                    "Invoice",
                    CustomizationIdentifier.valueOf("urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope"),
                    "1.0").toString();
        }
        return new PeppolDocumentTypeId(
                document.getRootNameSpace(),
                document.getRootNodeName(),
                CustomizationIdentifier.valueOf(document.getCustomizationId()),
                document.getVersionId()).toString();
    }

    private static boolean isSvefakturaObjectEnvelope(String customizationId) {
        return customizationId.contains("urn:sfti:services:documentprocessing:BasicInvoice:1:0###" +
                "urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope");
    }
}
