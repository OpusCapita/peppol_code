package com.opuscapita.peppol.eventing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class EventingController {

    @NotNull
    public PeppolEvent process(@NotNull ContainerMessage cm) {
        PeppolEvent result = new PeppolEvent();
        result.setFileName(cm.getFileName());

        if (cm.getBaseDocument() != null) {
            result.setInvoiceId(cm.getBaseDocument().getDocumentId());
            result.setInvoiceDate(cm.getBaseDocument().getIssueDate());
            result.setDueDate(cm.getBaseDocument().getDueDate());
            result.setRecipientId(cm.getBaseDocument().getRecipientId());
            result.setSenderId(cm.getBaseDocument().getSenderId());
            result.setRecipientName(cm.getBaseDocument().getRecipientName());
            result.setSenderName(cm.getBaseDocument().getSenderName());
            result.setSenderCountryCode(cm.getBaseDocument().getSenderCountryCode());
            result.setRecipientCountryCode(cm.getBaseDocument().getRecipientCountryCode());

            result.setTransactionId(cm.getTransactionId());

            if (cm.getBaseDocument() instanceof InvalidDocument) {
                InvalidDocument invalid = (InvalidDocument) cm.getBaseDocument();
                result.setErrorMessage(invalid.getReason());
            }
        }

        return result;
    }

}
