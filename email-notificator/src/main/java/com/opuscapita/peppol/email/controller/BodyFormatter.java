package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.validation.ValidationError;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * Moved to a separate class in order to have the possibility to introduce templates later.
 *
 * Works only if container message represents proper document, either invalid or valid.
 *
 * @author Sergejs.Roze
 */
@Component
public class BodyFormatter {

    @NotNull
    public String format(@NotNull ContainerMessage cm) {
        if (cm.getBaseDocument() == null) {
            throw new IllegalArgumentException("Container message has no document set");
        }

        if (cm.getBaseDocument() instanceof InvalidDocument) {
            return formatDocument(cm) + formatInvalid(cm);
        } else {
            return formatDocument(cm) + formatValidationErrors(cm);
        }
    }

    private String formatValidationErrors(ContainerMessage cm) {
        String result = "";
        if (cm.getValidationResult() != null && cm.getValidationResult().getErrors() != null) {
            for (ValidationError error : cm.getValidationResult().getErrors()) {
                result += "\n" + error.toString();
            }
        } else {
            result += "\n- ERROR               : Unspecified validation error";
        }

        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private String formatInvalid(ContainerMessage cm) {
        InvalidDocument doc = (InvalidDocument) cm.getBaseDocument();

        String result = "";
        boolean someInfoProvided = false;

        if (StringUtils.isNotBlank(doc.getReason())) {
            result += "\n- REASON              : " + doc.getReason();
            someInfoProvided = true;
        }
        if (doc.getException() != null) {
            result += "\n- EXCEPTION           : " + ExceptionUtils.getRootCauseMessage(doc.getException());
            result += "\n" + ExceptionUtils.getStackTrace(doc.getException());
            someInfoProvided = true;
        }

        if (!someInfoProvided) {
            result += "\n- REASON              : Unspecified document parsing error";
        }

        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private String formatDocument(ContainerMessage cm) {
        BaseDocument doc = cm.getBaseDocument();

        String result = "\n********************************************************************************";
        result += "\n- INVID               : " + doc.getDocumentId();
        result += "\n- BILLERID            : " + doc.getSenderId();
        result += "\n- BILLERNAME1         : " + doc.getSenderName();
        result += "\n- INVOICEEID          : " + doc.getRecipientId();
        result += "\n- INVOICEENAME1       : " + doc.getRecipientName();
        result += "\n- INVOICEDATE         : " + doc.getIssueDate();
        if (StringUtils.isNotBlank(doc.getDueDate())) {
            result += "\n- DUEDATE             : " + doc.getDueDate();
        }

        return result;
    }

}
