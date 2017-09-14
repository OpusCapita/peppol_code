package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
        List<String> errorList = collectErrors(cm);

        if (errorList.isEmpty() ) {
            result.append("\n- ERROR               : Unspecified error");
        }
        else if (errorList.size() == 1) {
            result.append("\n- ERROR               : ");
            result.append(errorList.get(0));
        } else {
            result.append("\n- ERRORS              : ");
            errorList.forEach(er -> result.append("\n\t").append(er));
        }
        return result.toString();
    }

    private List<String> collectErrors(ContainerMessage cm) {
        List<String> errors = new ArrayList<>();
        String errorText;
        if (cm.getProcessingInfo()!= null && (errorText = cm.getProcessingInfo().getProcessingException()) != null) {
            errors.add(errorText);
        }

        if (cm.getDocumentInfo() != null) {
            cm.getDocumentInfo().getErrors().forEach(er -> errors.add(er.toString()));
        }
        return errors;
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
