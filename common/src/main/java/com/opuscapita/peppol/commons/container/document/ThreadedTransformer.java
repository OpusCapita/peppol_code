package com.opuscapita.peppol.commons.container.document;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

/**
 * Created by bambr on 17.25.2.
 */
public class ThreadedTransformer {


    public byte[] getDocumentBytes(boolean omitXmlDeclaration, Source domSource) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            if (omitXmlDeclaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            try {
                transformer.transform(domSource, result);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        transformer.reset();
        return bos.toByteArray();
    }
}
