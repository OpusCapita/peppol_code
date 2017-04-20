package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import org.apache.commons.lang3.StringUtils;
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
    String format(@NotNull ContainerMessage cm) {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Container message has no document set");
        }
        return formatDocument(cm) + formatErrors(cm);
    }

    private String formatErrors(ContainerMessage cm) {
        StringBuilder result = new StringBuilder();
        if (cm.getDocumentInfo() != null && cm.getDocumentInfo().getErrors().size() > 0) {
            if (cm.getDocumentInfo().getErrors().size() == 1) {
                result.append("\n- ERROR               : ");
                result.append(cm.getDocumentInfo().getErrors().get(0).toString());
            } else {
                result.append("\n- ERRORS              : ");
                for (DocumentError error : cm.getDocumentInfo().getErrors()) {
                    result.append("\n\t").append(error.toString());
                }
            }
        } else {
            result.append("\n- ERROR               : Unspecified validation error");
        }

        return result.toString();
    }

    @SuppressWarnings("ConstantConditions")
    private String formatDocument(ContainerMessage cm) {
        DocumentInfo doc = cm.getDocumentInfo();

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
