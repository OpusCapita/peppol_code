package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

/**
 * Created by bambr on 17.20.2.
 */
@Component
public class SenderReceiverConsistencyValidator {

    private static final Logger logger = LoggerFactory.getLogger(SenderReceiverConsistencyValidator.class);

    public ValidationResult senderAndReceiverAreSame(ContainerMessage message) {
        ValidationResult result = new ValidationResult(message.getBaseDocument().getArchetype());

        BaseDocument baseDocument = message.getBaseDocument();

        Node sbdh = DocumentUtils.getSBDH(baseDocument.getDocument());
        Node root = baseDocument.getRootNode();

        ParticipantId sbdhSender = new ParticipantId(DocumentUtils.selectValueFrom(null, sbdh, "Sender", "Identifier"));
        ParticipantId documentSender = new ParticipantId(DocumentUtils.selectValueFrom(null, root, "AccountingSupplierParty", "Party", "EndpointID"));
        documentSender = new ParticipantId(DocumentUtils.selectValueFrom(documentSender.getRawValue(), root, "AccountingSupplierParty", "Party", "PartyLegalEntity", "CompanyID"));


        ParticipantId sbdhReceiver = new ParticipantId(DocumentUtils.selectValueFrom(null, sbdh, "Receiver", "Identifier"));
        ParticipantId documentReceiver = new ParticipantId(DocumentUtils.selectValueFrom(null, root, "AccountingCustomerParty", "Party", "EndpointID"));
        documentReceiver = new ParticipantId(DocumentUtils.selectValueFrom(documentReceiver.getRawValue(), root, "AccountingCustomerParty", "Party", "PartyLegalEntity"));

        try {
            result.setPassed(sbdhSender.equals(documentSender) && sbdhReceiver.equals(documentReceiver));
        } catch (NullPointerException e) {

        }
        if (!result.isPassed()) {
            String errorMessage = getFilledErrorBuilder(message, sbdhSender, documentSender, sbdhReceiver, documentReceiver).toString();
            logger.warn(errorMessage);
            result.addError(new ValidationError(errorMessage));
        }

        return result;
    }

    private StringBuilder getFilledErrorBuilder(ContainerMessage message, ParticipantId sbdhSender, ParticipantId documentSender, ParticipantId sbdhReceiver, ParticipantId documentReceiver) {
        return new StringBuilder("Failed sender/receiver consistency check for following file: ")
                        .append(message.getFileName())
                        .append("\nsender id from sbdh: ")
                        .append(sbdhSender)
                        .append("\nsender id from document: ")
                        .append(documentSender)
                        .append("\nreceiver id from sbdh: ")
                        .append(sbdhReceiver)
                        .append("\nreceiver id from document: ")
                        .append(documentReceiver);
    }

    private class ParticipantId {
        String rawValue;
        String prefix;
        String participantId;

        public ParticipantId(String rawValue) {
            this.rawValue = rawValue;
            if(rawValue.contains(":")) {
                String[] parts = rawValue.split(":", 2);
                prefix = parts[0];
                participantId = parts[1];
            } else {
                prefix = null;
                participantId = rawValue;
            }
        }

        public String getRawValue() {
            return rawValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ParticipantId that = (ParticipantId) o;

            if (prefix != null && that.prefix != null) {
                if(!prefix.equals(that.prefix)) {
                    return false;
                }
            }
            return participantId != null ? participantId.equals(that.participantId) : that.participantId == null;
        }

        @Override
        public int hashCode() {
            int result = rawValue.hashCode();
            result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
            result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return rawValue;
        }
    }


}
