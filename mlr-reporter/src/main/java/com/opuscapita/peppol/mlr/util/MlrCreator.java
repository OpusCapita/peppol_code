package com.opuscapita.peppol.mlr.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.Date;

/**
 * Creates UBL 2.0 MLR response files.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class MlrCreator {
    private final static Logger logger = LoggerFactory.getLogger(MlrCreator.class);

    private final OxalisErrorRecognizer oxalisErrorRecognizer;
    private final static MessageLevelResponseTemplates templates = new MessageLevelResponseTemplates();

    @Autowired
    public MlrCreator(@NotNull OxalisErrorRecognizer oxalisErrorRecognizer) {
        this.oxalisErrorRecognizer = oxalisErrorRecognizer;
    }

    public String reportSuccess(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        return reportError(cm);
    }

    public String reportRetry(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        return reportError(cm);
    }

    public String reportError(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        DocumentInfo di = cm.getDocumentInfo();
        if (di == null) {
            throw new IllegalArgumentException("Missing document info from container message");
        }

        String result = templates.getResponseTemplate();
        result = fillCommonFields(result, cm);
        result = setResponseCode(cm, result);
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private String setResponseCode(@NotNull ContainerMessage cm, @NotNull String template) {
        String description = templates.getDescriptionTemplate();
        if (!cm.hasErrors() && cm.getProcessingInfo().getProcessingException() == null) {
            if (cm.getProcessingInfo().getCurrentEndpoint().getType() == ProcessType.OUT_PEPPOL_RETRY) {
                description = replace(description, "description", cm.getProcessingInfo().getCurrentStatus());
                template = StringUtils.replace(template, "#DESCRIPTION#", description);
                return replace(template, "response_code", "AB");
            } else {
                template = StringUtils.replace(template, "#DESCRIPTION#", "");
                return replace(template, "response_code", "AP");
            }
        }

        if (StringUtils.isNotBlank(cm.getProcessingInfo().getProcessingException())) {
            // processing exception
            String msg = cm.getProcessingInfo().getProcessingException();
            description = replace(description, "description",oxalisErrorRecognizer.recognize(msg) + ": " + msg);
        } else {
            if (cm.getProcessingInfo().getCurrentEndpoint().getType() == ProcessType.OUT_VALIDATION ||
                    cm.getProcessingInfo().getCurrentEndpoint().getType() == ProcessType.IN_VALIDATION) {
                // validation issue
                description = replace(description, "description", "VALIDATION_ERROR");
            } else {
                // other document issue probably on preprocessing
                description = replace(description, "description", "DOCUMENT_ERROR");
            }
        }
        template = StringUtils.replace(template, "#DESCRIPTION#", description);
        return replace(template, "response_code", "RE");
    }

    @SuppressWarnings("ConstantConditions")
    private String fillCommonFields(@NotNull String template, @NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        DocumentInfo di = cm.getDocumentInfo();

        template = replace(template, "note", cm.getOriginalFileName());
        template = replace(template, "id", di.getDocumentId() + "-MLR");

        Date now = new Date();
        try {
            // issue date
            template = replace(template, "issue_date", MessageLevelResponseUtils.convertDateToXml(di.getIssueDate()));
            // issue time must always go after issue date
            if (StringUtils.isNotBlank(di.getIssueTime())) {
                String issue_time = templates.getIssueTimeTemplate();
                try {
                    issue_time = replace(issue_time, "issue_time", MessageLevelResponseUtils.convertTimeToXml(di.getIssueTime()));
                } catch (Exception e) {
                    logger.info("Failed to parse issue time: '" + di.getIssueTime() + "' for message: " + cm.toLog());
                    if (di.getErrors().isEmpty()) {
                        cm.addError("Unable to parse issue time: '" + di.getIssueTime() + "'");
                    }
                }
                template = StringUtils.replace(template, "#ISSUE_TIME#", issue_time);
            }
        } catch (Exception e) {
            logger.info("Failed to parse issue date: '" + di.getIssueDate() + "', using current date instead");
            template = replace(template, "issue_date", MessageLevelResponseUtils.convertDateToXml(now));
            if (di.getErrors().isEmpty()) {
                cm.addError("Unable to parse issue date: '" + di.getIssueDate() + "'");
            }
        }
        // if not replaced - remove placeholders
        template = StringUtils.replace(template, "#ISSUE_DATE#", "");
        template = StringUtils.replace(template, "#ISSUE_TIME#", "");

        template = replace(template, "response_date", MessageLevelResponseUtils.convertDateToXml(now));
        template = replace(template, "response_time", MessageLevelResponseUtils.convertTimeToXml(now));
        template = replace(template, "sender_id", di.getSenderId());
        template = replace(template, "sender_name", di.getSenderName());
        template = replace(template, "recipient_id", di.getRecipientId());
        template = replace(template, "recipient_name", di.getRecipientName());
        template = replace(template, "doc_reference", di.getDocumentBusinessIdentifier());

        template = StringUtils.replace(template, "#LINES#", createLines(cm));

        return template;
    }

    @SuppressWarnings("ConstantConditions")
    private String createLines(@NotNull ContainerMessage cm) {
        DocumentInfo di = cm.getDocumentInfo();
        if (di.getErrors().isEmpty() && di.getWarnings().isEmpty() && cm.getProcessingInfo().getProcessingException() == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (DocumentError error : di.getErrors()) {
            String template = templates.getLineTemplate();
            template = replace(template, "doc_reference", di.getDocumentId());

            String xpath = "NA";
            String referenceId = error.getSource().getName();
            String description = error.getMessage();
            String errorCode = "SV";
            if (error.getValidationError() != null) {
                xpath = error.getValidationError().getLocation();
                referenceId = error.getValidationError().getIdentifier();
                description = error.getValidationError().getDetails();
                errorCode = "RVF";
            }
            template = replace(template, "xpath", xpath);
            template = replace(template, "reference_id", referenceId);
            template = replace(template, "description", description);
            template = replace(template, "status_code", errorCode);
            result.append(template);
        }

        for (DocumentWarning warning : di.getWarnings()) {
            String template = templates.getLineTemplate();
            template = replace(template, "doc_reference", di.getDocumentId());

            if (warning.getValidationError() != null) {
                template = replace(template, "xpath", warning.getValidationError().getLocation());
                template = replace(template, "reference_id", warning.getValidationError().getIdentifier());
                template = replace(template, "description", warning.getValidationError().getDetails());
                template = replace(template, "status_code", "RVW");
            }
            result.append(template);
        }

        return result.toString();
    }

    private String replace(@NotNull String original, @NotNull String key, @Nullable String value) {
        if (value == null) {
            value = "";
        }
        value = StringEscapeUtils.escapeXml10(value);
        value = StringUtils.replace(value, "&apos;", "'"); // requested by Sweden
        value = StringUtils.replace(value, "&quot;", "\"");
        return StringUtils.replace(original, "${" + key + "}", value);
    }

}
