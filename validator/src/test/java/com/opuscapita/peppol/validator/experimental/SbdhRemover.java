package com.opuscapita.peppol.validator.experimental;

import com.opuscapita.peppol.commons.container.document.DocumentContentUtils;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Just an example of StAX based SBDH remover.
 *
 * @author Sergejs.Roze
 */
public class SbdhRemover {

    public static void main3(String[] args) throws ParserConfigurationException, IOException, SAXException, InterruptedException, ExecutionException, TimeoutException, TransformerException {
        Date now = new Date();
        System.gc();
        long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (int i = 0; i < 10000; i++) {
            try (InputStream inputStream = SbdhRemover.class.getResourceAsStream("/test_data/difi_files/EHF_profile-bii05_invoice_invalid.xml")) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(inputStream);

                Node root = DocumentUtils.getRootNode(document);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document newDocument = builder.newDocument();

                Node importedNode = newDocument.importNode(root, true);
                newDocument.appendChild(importedNode);
                byte[] data = DocumentContentUtils.getDocumentBytes(newDocument);

                System.out.println(i + " " + new String(data).length());
            }
        }

        System.out.println(now);
        System.out.println(ram);
        System.out.println(new Date());
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        System.gc();
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }

    public static void main(String[] args) throws IOException, XMLStreamException {

        Date now = new Date();
        System.gc();
        long ram = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        for (int i = 0; i < 10000; i++) {
            try (InputStream inputStream = SbdhRemover.class.getResourceAsStream("/test_data/difi_files/EHF_profile-bii05_invoice_invalid.xml")) {
                XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Writer writer = new OutputStreamWriter(outputStream);

                boolean collecting = false;
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement start = event.asStartElement();
                        String name = start.getName().getLocalPart();

                        if ("Invoice".equals(name)) {
                            collecting = true;
                        }
                        if ("Attachment".equals(name)) {
                            collecting = false;
                        }
                    }

                    if (collecting) {
                        event.writeAsEncodedUnicode(writer);
                    }

                    if (event.isEndElement()) {
                        EndElement end = event.asEndElement();
                        String name = end.getName().getLocalPart();
                        if ("Invoice".equals(name)) {
                            break;
                        }
                        if ("Attachment".equals(name)) {
                            collecting = true;
                        }
                    }
                }
                writer.close();

                System.out.println(i + " " + new String(outputStream.toByteArray()).length());
            }
        }
        System.out.println(now);
        System.out.println(ram);
        System.out.println(new Date());
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        System.gc();
        System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());

    }

}
