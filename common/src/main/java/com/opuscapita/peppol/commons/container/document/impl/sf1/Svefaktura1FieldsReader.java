package com.opuscapita.peppol.commons.container.document.impl.sf1;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import static com.opuscapita.peppol.commons.container.document.DocumentUtils.selectValueFrom;

/**
 * @author Sergejs.Roze
 */
public class Svefaktura1FieldsReader implements FieldsReader {

    @Override
    public boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base) {
        boolean success = true;

        String value = selectValueFrom(null, sbdh, "Sender", "Identifier");
        value = selectValueFrom(value, root, "SellerParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "SellerParty", "Party", "PartyIdentification", "ID");
        if (value == null) {
            success = false;
        } else {
            base.setSenderId(value);
        }

        value = selectValueFrom(null, sbdh, "Receiver", "Identifier");
        value = selectValueFrom(value, root, "BuyerParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "BuyerParty", "Party", "PartyTaxScheme", "CompanyID");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientId(value);
        }

        base.setVersionId("1.0");

        value = selectValueFrom(null, root, "ID");
        if (value == null) {
            success = false;
        } else {
            base.setDocumentId(value);
        }

        value = selectValueFrom(null, root, "IssueDate");
        if (value == null) {
            success = false;
        } else {
            base.setIssueDate(value);
        }

        value = selectValueFrom(null, root, "PaymentMeans", "DuePaymentDate");
        if (value == null) {
            success = false;
        } else {
            base.setDueDate(value);
        }

        value = selectValueFrom(null, root, "SellerParty", "Party", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setSenderName(value);
        }

        value = selectValueFrom(null, root, "SellerParty", "Party", "Address", "Country", "IdentificationCode");
        if (value == null) {
            success = false;
        } else {
            base.setSenderCountryCode(value);
        }

        value = selectValueFrom(null, root, "BuyerParty", "Party", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientName(value);
        }

        value = selectValueFrom(null, root, "BuyerParty", "Party", "Address", "Country", "IdentificationCode");
        if (value == null) {
            base.setRecipientCountryCode(base.getSenderCountryCode());
        } else {
            base.setRecipientCountryCode(value);
        }

        base.setProfileId("urn:sfti:services:documentprocessing:BasicInvoice:1:0");

        Node object = DocumentUtils.searchForChildNode(root.getParentNode(), DocumentUtils.OBJECT_ENVELOPE);
        if (object == null) {
            base.setCustomizationId("urn:sfti:documents:BasicInvoice:1:0::Invoice##urn:sfti:documents:BasicInvoice:1:0::1.0");
        } else {
            base.setCustomizationId(
                    "urn:sfti:documents:StandardBusinessDocumentHeader::Invoice##urn:sfti:documents:BasicInvoice:1:0:#BasicInvoice_ObjectEnvelope::1.0");
        }

        return success;
    }

}
