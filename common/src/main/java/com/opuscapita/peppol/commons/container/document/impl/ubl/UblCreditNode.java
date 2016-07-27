package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import static com.opuscapita.peppol.commons.container.document.DocumentUtils.selectValueFrom;

/**
 * @author Sergejs.Roze
 */
public class UblCreditNode extends UblInvoice {

    @Override
    public boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base) {
        boolean success = true;

        String value = selectValueFrom(null, sbdh, "Sender", "Identifier");
        value = selectValueFrom(value, root, "AccountingSupplierParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "AccountingSupplierParty", "Party", "PartyLegalEntity", "CompanyID");
        if (value == null) {
            success = false;
        } else {
            base.setSenderId(value);
        }

        value = selectValueFrom(null, sbdh, "Receiver", "Identifier");
        value = selectValueFrom(value, root, "AccountingCustomerParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "AccountingCustomerParty", "Party", "PartyLegalEntity");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientId(value);
        }

        value = selectValueFrom(null, root, "AccountingSupplierParty", "Party", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setSenderName(value);
        }

        value = selectValueFrom(null, root, "AccountingSupplierParty", "Party", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            success = false;
        } else {
            base.setSenderCountryCode(value);
        }

        value = selectValueFrom(null, root, "AccountingCustomerParty", "Party", "PartyName", "Name");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientName(value);
        }

        value = selectValueFrom(null, root, "AccountingCustomerParty", "Party", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            success = false;
        } else {
            base.setRecipientCountryCode(value);
        }

        return UblInvoice.fillCommonFields(sbdh, root, base) && success;
    }
}