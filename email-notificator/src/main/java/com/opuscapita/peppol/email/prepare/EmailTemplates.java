package com.opuscapita.peppol.email.prepare;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.email.model.Recipient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Moved to a separate class in order to have the possibility to introduce templates later.
 *
 * Works only if container message represents proper document, either valid or invalid.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class EmailTemplates {

    public static String getTicketHeader(@NotNull String recipientId, @NotNull String recipientName) {
        return "Email notification about the invalid data was sent to " + recipientId + " " + recipientName;
    }

    @NotNull
    public static String format(@NotNull ContainerMessage cm) {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Container message has no document set");
        }
        return formatDocument(cm) + formatErrors(cm);
    }

    private static String formatErrors(ContainerMessage cm) {
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

    private static List<String> collectErrors(ContainerMessage cm) {
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
    private static String formatDocument(ContainerMessage cm) {
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

    public static String getTicketFirstLine(@NotNull Recipient recipient, @NotNull String transmissionIds) {
        String result;
        if (recipient.getType() == Recipient.Type.AP) {
            result = "Email notification about the invalid data " + transmissionIds + " was sent to the AP ";
        } else {
            result = "Email notification about the invalid data " + transmissionIds + " was sent to the customer ";
        }
        return result + recipient.getId() + " " + recipient.getName();
    }

    public static String getEmailFirstLine() {
        return "This is an automatically redirected electronic invoice rejection message:\n\n" +
                "The following PEPPOL invoice(s) have been rejected by the operator (see Subject).\n\n" +
                "Please correct invoice(s) and resend.\n\n" +
                "If you have any questions concerning the rejection, please reply directly to this e-mail.\n\n";
    }

}
