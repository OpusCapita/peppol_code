package com.opuscapita.peppol.outbound.util;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import eu.peppol.identifier.CustomizationIdentifier;
import eu.peppol.identifier.PeppolDocumentTypeId;
import org.jetbrains.annotations.NotNull;

/**
 * Moved from base document to a separate utility class.
 *
 * @author Sergejs.Roze
 */
public class OxalisUtils {

    public static PeppolDocumentTypeId getPeppolDocumentTypeId(@NotNull BaseDocument document) {
        return new PeppolDocumentTypeId(
                document.getRootNode().getNamespaceURI(),
                document.getRootNode().getLocalName(),
                new CustomizationIdentifier(document.getCustomizationId()),
                document.getVersionId()
        );

    }

}
