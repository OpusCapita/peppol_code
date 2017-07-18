package com.opuscapita.peppol.eventing.destinations.mlr;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_21.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

/**
 * Creates UBL 2.0 MLR response files.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class MessageLevelResponseCreator {

    /**
     * Creates MLR file about an error.
     *
     * @param cm the container message
     */
    public ApplicationResponseType reportError(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        return null; // TODO
    }

    /**
     * Creates MLR file about a successfull end of processing.
     *
     * @param cm the container message
     */
    public ApplicationResponseType reportSuccess(@NotNull ContainerMessage cm) throws ParseException, DatatypeConfigurationException {
        DocumentInfo di = cm.getDocumentInfo();
        if (di == null) {
            throw new IllegalArgumentException("Missing document info from container message");
        }

        ApplicationResponseType art = new ApplicationResponseType();
        art.setID(di.getDocumentId() + "-MLR");
        art.setIssueDate(MlrUtils.convertToXml(di.getIssueDate()));
        art.setResponseDate(MlrUtils.convertToXml(new Date()));
        art.setSenderParty(createParty(di.getSenderId(), di.getSenderName()));
        art.setReceiverParty(createParty(di.getRecipientId(), di.getRecipientName()));

        art.setDocumentResponse(Collections.singletonList(createDocumentResponseType("AP", di.getDocumentId())));

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

    private DocumentResponseType createDocumentResponseType(@NotNull String responseCode, @NotNull String documentId) {
        DocumentResponseType result = new DocumentResponseType();

        ResponseType rt = new ResponseType();
        rt.setResponseCode(responseCode);
        result.setResponse(rt);

        DocumentReferenceType drt = new DocumentReferenceType();
        drt.setID(documentId);
        result.setDocumentReference(Collections.singletonList(drt));

        return result;
    }
}
