package com.opuscapita.peppol.validator.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

/**
 * Created by bambr on 16.3.10.
 */
public class DocumentContentUtils {
    public static byte[] getDocumentBytes(ContainerMessage containerMessage) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(containerMessage.getBaseDocument().getDocument());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        transformer.transform(source, result);
        return bos.toByteArray();
    }

    public static byte[] nodeToByteArray(Node node) throws TransformerException {
        DOMSource domSource = new DOMSource(node);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, result);
        return bos.toByteArray();
    }
}
