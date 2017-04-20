package com.opuscapita.peppol.tools.rest;

import com.helger.peppol.identifier.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppol.identifier.peppol.participant.PeppolParticipantIdentifier;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.smp.*;
import com.helger.peppol.smpclient.SMPClientReadOnly;
import com.helger.peppol.smpclient.exception.SMPClientException;
import com.helger.peppol.url.PeppolURLProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.NodeList;

import javax.xml.transform.dom.DOMResult;

/**
 * Created by bambr on 17.19.4.
 */
@RestController
public class SmpLookup {

    @RequestMapping(path = "/", method = RequestMethod.GET)
    @ResponseBody
    String lookupParticipant(String participantId) throws SMPClientException {
        String result = "";

        // The PEPPOL participant identifier
        final PeppolParticipantIdentifier aPI_AT_Test = PeppolParticipantIdentifier.createWithDefaultScheme(/*"9908:971032081"*/ participantId);

        // Create the main SMP client using the production SML
        final SMPClientReadOnly aSMPClient = new SMPClientReadOnly(PeppolURLProvider.INSTANCE,
                aPI_AT_Test,
                ESML.DIGIT_PRODUCTION);

        SignedServiceMetadataType serviceRegistration = aSMPClient.getServiceRegistration(aPI_AT_Test, EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A_V20);
        ServiceMetadataType serviceMetaData = serviceRegistration.getServiceMetadata();
        ServiceInformationType serviceInformation = serviceMetaData.getServiceInformation();
        ProcessListType processList = serviceInformation.getProcessList();
        for (int i = 0; i < processList.getProcessCount(); i++) {
            ProcessType process = processList.getProcessAtIndex(i);
            ServiceEndpointList endpoints = process.getServiceEndpointList();
            for (int j = 0; j < endpoints.getEndpointCount(); j++) {
                EndpointType endpoint = endpoints.getEndpointAtIndex(j);
                DOMResult domResult = new DOMResult();
                endpoint.getEndpointReference().writeTo(domResult);
                NodeList childNodes = domResult.getNode().getChildNodes().item(0).getChildNodes();
                String address = childNodes.item(0).getTextContent();
                result = endpoint.getServiceDescription() + "###" + address;
            }
        }


        /*final String sEndpointAddress = aSMPClient.getEndpointAddress(aPI_AT_Test,
                EPredefinedDocumentTypeIdentifier.INVOICE_T010_BIS4A_V20,
                EPredefinedProcessIdentifier.BIS4A,
                ESMPTransportProfile.TRANSPORT_PROFILE_AS2);
        result = "Result: " + sEndpointAddress;*/

        return result;
    }
}
