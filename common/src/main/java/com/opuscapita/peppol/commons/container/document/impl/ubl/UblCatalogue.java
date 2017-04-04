package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

/**
 * @author Sergejs.Roze
 */
public class UblCatalogue implements FieldsReader {

    @Override
    public boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull DocumentInfo base) {
        boolean success = true;

//        String sbdhValue = selectValueFrom(null, sbdh, "Sender", "Identifier");
//        String value = selectValueFrom(null, root, "SellerSupplierParty", "Party", "EndpointID");
//        value = selectValueFrom(value, root, "SellerSupplierParty", "Party", "PartyIdentification", "ID");
//        boolean result = SbdhDocumentConsistencyChecker.doTheCheck(value, sbdhValue, "sender id", base, (String checkedValue, DocumentInfo baseDocument) -> baseDocument.setSenderId(checkedValue));
//        if (!result) {
//            success = false;
//        }
//
//        sbdhValue = selectValueFrom(null, sbdh, "Receiver", "Identifier");
//        value = selectValueFrom(null, root, "ReceiverParty", "EndpointID");
//        value = selectValueFrom(value, root, "ReceiverParty", "PartyIdentification", "ID");
//        result = SbdhDocumentConsistencyChecker.doTheCheck(value, sbdhValue, "receiver id", base, (String checkedValue, DocumentInfo documentInfo) -> documentInfo.setRecipientId(checkedValue));
//        if (!result) {
//            success = false;
//        }
//
//        value = selectValueFrom(null, root, "SellerSupplierParty", "Party", "PartyName", "Name");
//        if (value == null) {
//            success = false;
//        } else {
//            base.setSenderName(value);
//        }
//
//        value = selectValueFrom(null, root, "SellerSupplierParty", "Party", "PostalAddress", "Country", "IdentificationCode");
//        if (value == null) {
//            success = false;
//        } else {
//            base.setSenderCountryCode(value);
//        }
//
//        value = selectValueFrom(null, root, "ReceiverParty", "PartyName", "Name");
//        if (value == null) {
//            success = false;
//        } else {
//            base.setRecipientName(value);
//        }
//
//        value = selectValueFrom(null, root, "ReceiverParty", "PostalAddress", "Country", "IdentificationCode");
//        if (value == null) {
//            success = false;
//        } else {
//            base.setRecipientCountryCode(value);
//        }
//
//        return UblInvoice.fillCommonFields(sbdh, root, base) && success;
        return true;
    }

}
