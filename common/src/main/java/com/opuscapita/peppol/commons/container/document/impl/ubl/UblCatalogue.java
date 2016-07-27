package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import static com.opuscapita.peppol.commons.container.document.DocumentUtils.selectValueFrom;

/**
 * @author Sergejs.Roze
 */
public class UblCatalogue implements FieldsReader {

    @Override
    public boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base) {
        boolean success = true;

        String value = selectValueFrom(null, sbdh, "Sender", "Identifier");
        value = selectValueFrom(value, root, "SellerSupplierParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "SellerSupplierParty", "Party", "PartyIdentification", "ID");
        if (value == null) {
            success = false;
        } else {
            base.setSenderId(value);
        }

        value = selectValueFrom(null, sbdh, "Receiver", "Identifier");
        value = selectValueFrom(value, root, "ReceiverParty", "EndpointID");
        value = selectValueFrom(value, root, "ReceiverParty", "PartyIdentification", "ID");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientId(value);
        }

        value = selectValueFrom(null, root, "SellerSupplierParty", "Party", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setSenderName(value);
        }

        value = selectValueFrom(null, root, "SellerSupplierParty", "Party", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            success = false;
        } else {
            base.setSenderCountryCode(value);
        }

        value = selectValueFrom(null, root, "ReceiverParty", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientName(value);
        }

        value = selectValueFrom(null, root, "ReceiverParty", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientCountryCode(value);
        }

        return UblInvoice.fillCommonFields(sbdh, root, base) && success;

    }

}
