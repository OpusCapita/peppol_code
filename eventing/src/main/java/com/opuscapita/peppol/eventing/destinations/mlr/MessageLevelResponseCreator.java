package com.opuscapita.peppol.eventing.destinations.mlr;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.validation.ValidationError;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IssueTimeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.XPathType;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Creates UBL 2.0 MLR response files.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class MessageLevelResponseCreator {
    private final static Logger logger = LoggerFactory.getLogger(MessageLevelResponseCreator.class);

    /**
     * Creates MLR file about an error.
     *
     * @param cm the container message
     */
    @SuppressWarnings("ConstantConditions")
    public ApplicationResponseType reportError(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        ApplicationResponseType result = commonPart(cm);
        DocumentInfo di = cm.getDocumentInfo();
        ProcessingInfo pi = cm.getProcessingInfo();
        if (pi == null) {
            throw new IllegalArgumentException("Missing processing info from the document");
        }

        String reason = "Document parse error";

        DocumentResponseType drt;
        if (StringUtils.isNotBlank(pi.getProcessingException())) {
            reason = pi.getProcessingException();
        }

        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_VALIDATION ||
                pi.getCurrentEndpoint().getType() == ProcessType.IN_VALIDATION) {
            drt = createDocumentResponseType("RE", di.getDocumentBusinessIdentifier(), "Validation error");
        } else {
            drt = createDocumentResponseType("RE", di.getDocumentBusinessIdentifier(), reason);
        }

        if (!di.getErrors().isEmpty()) {
            drt.setLineResponse(createLineResponse(di.getErrors(), di));
        }

        result.setDocumentResponse(Collections.singletonList(drt));
        return result;
    }

    public ApplicationResponseType reportRetry(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        ApplicationResponseType result = commonPart(cm);

        DocumentInfo di = cm.getDocumentInfo();
        if (di == null || !di.getErrors().isEmpty()) {
            return reportError(cm);
        }

        ProcessingInfo pi = cm.getProcessingInfo();
        if (pi == null) {
            throw new IllegalArgumentException("Missing processing info from the document");
        }

        DocumentResponseType documentResponse = createDocumentResponseType("AB", di.getDocumentBusinessIdentifier(), null);
        List<LineResponseType> warnings = createLineResponse(di.getWarnings(), di);
        if (!warnings.isEmpty()) {
            documentResponse.setLineResponse(warnings);
        }

        result.setDocumentResponse(Collections.singletonList(documentResponse));

        return result;
    }

    private List<LineResponseType> createLineResponse(List<? extends DocumentError> errors, DocumentInfo di) {
        List<LineResponseType> list = new ArrayList<>();

        for (DocumentError error : errors) {
            LineResponseType lineResponse = new LineResponseType();
            ValidationError validationError = error.getValidationError();

            LineReferenceType lineReference = new LineReferenceType();
            lineReference.setLineID("NA"); // where to get it from?

            DocumentReferenceType documentReference = new DocumentReferenceType();
            documentReference.setID(di.getDocumentId());
            if (validationError != null) {
                XPathType xPathType = new XPathType(validationError.getLocation());
                documentReference.setXPath(Collections.singletonList(xPathType));
            }
            lineReference.setDocumentReference(documentReference);
            lineResponse.setLineReference(lineReference);

            ResponseType response = new ResponseType();
            if (validationError != null) {
                response.setReferenceID(validationError.getIdentifier());
                response.setDescription(Collections.singletonList(new DescriptionType(validationError.getDetails())));
            } else {
                response.setDescription(Collections.singletonList(new DescriptionType(error.getMessage())));
            }

            StatusType status = new StatusType();
            if (error instanceof DocumentWarning) {
                status.setStatusReasonCode("RVW");
            } else {
                if (validationError != null) {
                    status.setStatusReasonCode("RVF");
                } else {
                    status.setStatusReasonCode("SV");
                }
            }
            response.setStatus(Collections.singletonList(status));
            lineResponse.setResponse(Collections.singletonList(response));
            list.add(lineResponse);
        }

        return list;
    }

    /**
     * Creates MLR file about a successfull end of processing.
     *
     * @param cm the container message
     */
    @SuppressWarnings("ConstantConditions")
    public ApplicationResponseType reportSuccess(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        ApplicationResponseType art = commonPart(cm);

        DocumentInfo di = cm.getDocumentInfo();
        if (!di.getErrors().isEmpty()) {
            return reportError(cm);
        }

        DocumentResponseType drt = createDocumentResponseType("AP", di.getDocumentBusinessIdentifier(), null);
        List<LineResponseType> warnings = createLineResponse(di.getWarnings(), di);
        if (!warnings.isEmpty()) {
            drt.setLineResponse(warnings);
        }

        art.setDocumentResponse(Collections.singletonList(drt));

        return art;
    }

    private ApplicationResponseType commonPart(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        DocumentInfo di = cm.getDocumentInfo();
        if (di == null) {
            throw new IllegalArgumentException("Missing document info from container message");
        }

        ApplicationResponseType art = new ApplicationResponseType();
        art.setNote(Collections.singletonList(new NoteType(cm.getOriginalFileName())));
        art.setID(di.getDocumentId() + "-MLR");

        Date now = new Date();
        try {
            // issue date
            art.setIssueDate(MessageLevelResponseUtils.convertToXml(di.getIssueDate()));
            // issue time must always go after issue date
            if (StringUtils.isNotBlank(di.getIssueTime())) {
                try {
                    art.setIssueTime(new IssueTimeType(MessageLevelResponseUtils.convertToXmlTime(di.getIssueTime())));
                } catch (Exception e) {
                    logger.info("Failed to parse issue time: '" + di.getIssueTime() + "'");
                    if (di.getErrors().isEmpty()) {
                        cm.addError("Unable to parse issue time: '" + di.getIssueTime() + "'");
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Failed to parse issue date: '" + di.getIssueDate() + "', using current date instead");
            art.setIssueDate(MessageLevelResponseUtils.convertToXml(now));
            if (di.getErrors().isEmpty()) {
                cm.addError("Unable to parse issue date: '" + di.getIssueDate() + "'");
            }
        }

        art.setResponseDate(MessageLevelResponseUtils.convertToXml(now));
        art.setResponseTime(MessageLevelResponseUtils.convertToXml(now));
        art.setSenderParty(createParty(di.getSenderId(), di.getSenderName()));
        art.setReceiverParty(createParty(di.getRecipientId(), di.getRecipientName()));

        return art;
    }

    private PartyType createParty(@NotNull String endpointId, @Nullable String partyName) {
        PartyType result = new PartyType();
        result.setEndpointID(endpointId);

        if (StringUtils.isNotBlank(partyName)) {
            PartyNameType pnt = new PartyNameType();
            pnt.setName(partyName);
            result.setPartyName(Collections.singletonList(pnt));
        }

        return result;
    }

    private DocumentResponseType createDocumentResponseType(@NotNull String responseCode, @NotNull String instanceIdentifier,
                                                            @Nullable String responseText) {
        DocumentResponseType result = new DocumentResponseType();

        ResponseType rt = new ResponseType();
        rt.setResponseCode(responseCode);

        if (StringUtils.isNotBlank(responseText)) {
            rt.setDescription(Collections.singletonList(new DescriptionType(responseText)));
        }

        result.setResponse(rt);

        DocumentReferenceType drt = new DocumentReferenceType();
        drt.setID(instanceIdentifier);
        result.setDocumentReference(Collections.singletonList(drt));

        return result;
    }
}
