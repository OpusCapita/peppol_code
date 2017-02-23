package com.opuscapita.peppol.commons.container.document.impl.ubl;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.SbdhDocumentConsistencyChecker;
import com.opuscapita.peppol.commons.container.document.impl.FieldsReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import static com.opuscapita.peppol.commons.container.document.DocumentUtils.selectValueFrom;

/**
 * @author Sergejs.Roze
 */
public class UblInvoice implements FieldsReader {
    private static final Logger logger = LoggerFactory.getLogger(UblInvoice.class);

    static boolean fillCommonFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base) {
        boolean success = true;

        String value = selectValueFrom(null, sbdh, "DocumentIdentification", "TypeVersion");
        value = selectValueFrom(value, root, "UBLVersionID");
        if (value == null) {
            success = false;
        } else {
            base.setVersionId(value);
        }

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

        value = selectValueFrom(null, root, "ProfileID");
        if (value == null) {
            success = false;
        } else {
            base.setProfileId(value);
        }

        value = selectValueFrom(null, root, "CustomizationID");
        if (value == null) {
            success = false;
        } else {
            base.setCustomizationId(value);
        }

        return success;
    }

    @Override
    public boolean fillFields(@Nullable Node sbdh, @NotNull Node root, @NotNull BaseDocument base) {
        boolean success = true;

        String sbdhValue = selectValueFrom(null, sbdh, "Sender", "Identifier");
        String value = selectValueFrom(null, root, "AccountingSupplierParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "AccountingSupplierParty", "Party", "PartyLegalEntity", "CompanyID");
        /*if (value == null && sbdhValue == null) {
            logger.warn("Failed to find sender ID field");
            success = false;
        } else if(sbdh!= null && sbdhValue != null) {
            ParticipantId sbdhSender = new ParticipantId(sbdhValue);
            ParticipantId documentSender = new ParticipantId(value);
            if(!sbdhSender.equals(documentSender)) {
                logger.warn("SBDH and document have different value for sender: " + sbdhValue + " <> " + value);
                success = false;
            }  else {
                base.setSenderId(sbdhValue != null? sbdhValue :value);
            }
        } else {
            base.setSenderId(value);
        }*/
        boolean result = SbdhDocumentConsistencyChecker.doTheCheck(value, sbdhValue, "sender id", base, (String checkedValue, BaseDocument baseDocument) -> baseDocument.setSenderId(checkedValue));
        if (!result) {
            success = false;
        }

        sbdhValue = selectValueFrom(null, sbdh, "Receiver", "Identifier");
        value = selectValueFrom(null, root, "AccountingCustomerParty", "Party", "EndpointID");
        value = selectValueFrom(value, root, "AccountingCustomerParty", "Party", "PartyLegalEntity");
        /*if (value == null && sbdhValue == null) {
            logger.warn("Failed to find recipient ID field");
            success = false;
        } else if(sbdh!= null && sbdhValue != null) {
            ParticipantId sbdhReceiver = new ParticipantId(sbdhValue);
            ParticipantId documentReceiver = new ParticipantId(value);
            if(!sbdhReceiver.equals(documentReceiver)) {
                logger.warn("SBDH and document have different value for receiver: " + sbdhValue + " <> " + value);
                success = false;
            } else {
                base.setRecipientId(sbdhValue != null? sbdhValue :value);
            }
        } else {
            base.setRecipientId(value);
        }*/
        result = SbdhDocumentConsistencyChecker.doTheCheck(value, sbdhValue, "receiver id", base, (String checkedValue, BaseDocument baseDocument) -> baseDocument.setRecipientId(checkedValue));
        if (!result) {
            success = false;
        }

        value = selectValueFrom(null, root, "AccountingSupplierParty", "Party", "PartyName", "Name");
        if (value == null) {
            logger.warn("Failed to find sender name");
            success = false;
        } else {
            base.setSenderName(value);
        }

        value = selectValueFrom(null, root, "AccountingSupplierParty", "Party", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            logger.warn("Failed to find sender country code");
            success = false;
        } else {
            base.setSenderCountryCode(value);
        }

        value = selectValueFrom(null, root, "AccountingCustomerParty", "Party", "PartyName", "Name");
        if (value == null) {
            logger.warn("Failed to find recipient name");
            success = false;
        } else {
            base.setRecipientName(value);
        }

        value = selectValueFrom(null, root, "AccountingCustomerParty", "Party", "PostalAddress", "Country", "IdentificationCode");
        if (value == null) {
            logger.warn("Failed to find recipient country code");
            success = false;
        } else {
            base.setRecipientCountryCode(value);
        }

        value = selectValueFrom(null, root, "PaymentMeans", "PaymentDueDate");
        if (value != null) {
            base.setDueDate(value);
        }

        return fillCommonFields(sbdh, root, base) && success;
    }

}
