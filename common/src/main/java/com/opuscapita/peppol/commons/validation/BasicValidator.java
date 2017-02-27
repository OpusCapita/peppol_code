package com.opuscapita.peppol.commons.validation;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentContentUtils;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Daniil on 03.05.2016.
 */
public interface BasicValidator {

    ValidationResult validate(byte[] data);

    default byte[] extractInvoice(ContainerMessage containerMessage) throws TransformerException, InterruptedException, ExecutionException, TimeoutException {
        NodeList invoiceNodes = containerMessage.getBaseDocument().getDocument().getElementsByTagNameNS("Invoice", "*");
        return DocumentContentUtils.nodeToByteArray(invoiceNodes.item(0));
    }
}
