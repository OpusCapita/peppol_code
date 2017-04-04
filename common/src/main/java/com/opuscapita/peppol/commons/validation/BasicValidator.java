package com.opuscapita.peppol.commons.validation;

/**
 * Created by Daniil on 03.05.2016.
 */
public interface BasicValidator {

    ValidationResult validate(byte[] data);

//    default byte[] extractInvoice(ContainerMessage containerMessage) throws TransformerException, InterruptedException, ExecutionException, TimeoutException {
//        NodeList invoiceNodes = containerMessage.getDocumentInfo().getDocument().getElementsByTagNameNS("Invoice", "*");
//        return DocumentContentUtils.nodeToByteArray(invoiceNodes.item(0));
//    }
}
