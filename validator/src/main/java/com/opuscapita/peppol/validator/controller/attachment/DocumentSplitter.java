package com.opuscapita.peppol.validator.controller.attachment;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;

/**
 * Reads document from storage and splits it into two parts - SBDH and document body.
 *
 * @author Sergejs.Roze
 */
@Component
public class DocumentSplitter {
    final static String MINIMAL_PDF =
            "JVBERi0xLjEKJcKlwrHDqwoKMSAwIG9iagogIDw8IC9UeXBlIC9DYXRhbG9nCiAgICAgL1BhZ2Vz\n" +
            "IDIgMCBSCiAgPj4KZW5kb2JqCgoyIDAgb2JqCiAgPDwgL1R5cGUgL1BhZ2VzCiAgICAgL0tpZHMg\n" +
            "WzMgMCBSXQogICAgIC9Db3VudCAxCiAgICAgL01lZGlhQm94IFswIDAgMzAwIDE0NF0KICA+Pgpl\n" +
            "bmRvYmoKCjMgMCBvYmoKICA8PCAgL1R5cGUgL1BhZ2UKICAgICAgL1BhcmVudCAyIDAgUgogICAg\n" +
            "ICAvUmVzb3VyY2VzCiAgICAgICA8PCAvRm9udAogICAgICAgICAgIDw8IC9GMQogICAgICAgICAg\n" +
            "ICAgICA8PCAvVHlwZSAvRm9udAogICAgICAgICAgICAgICAgICAvU3VidHlwZSAvVHlwZTEKICAg\n" +
            "ICAgICAgICAgICAgICAgL0Jhc2VGb250IC9UaW1lcy1Sb21hbgogICAgICAgICAgICAgICA+Pgog\n" +
            "ICAgICAgICAgID4+CiAgICAgICA+PgogICAgICAvQ29udGVudHMgNCAwIFIKICA+PgplbmRvYmoK\n" +
            "CjQgMCBvYmoKICA8PCAvTGVuZ3RoIDU1ID4+CnN0cmVhbQogIEJUCiAgICAvRjEgMTggVGYKICAg\n" +
            "IDAgMCBUZAogICAgKEhlbGxvIFdvcmxkKSBUagogIEVUCmVuZHN0cmVhbQplbmRvYmoKCnhyZWYK\n" +
            "MCA1CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwMDAxOCAwMDAwMCBuIAowMDAwMDAwMDc3IDAw\n" +
            "MDAwIG4gCjAwMDAwMDAxNzggMDAwMDAgbiAKMDAwMDAwMDQ1NyAwMDAwMCBuIAp0cmFpbGVyCiAg\n" +
            "PDwgIC9Sb290IDEgMCBSCiAgICAgIC9TaXplIDUKICA+PgpzdGFydHhyZWYKNTY1CiUlRU9GCg==";

    private final XMLInputFactory xmlInputFactory;
    private final AttachmentValidator attachmentValidator;

    public DocumentSplitter(@NotNull @Lazy XMLInputFactory xmlInputFactory, @NotNull AttachmentValidator attachmentValidator) {
        this.xmlInputFactory = xmlInputFactory;
        this.attachmentValidator = attachmentValidator;
    }

    /**
     * Returns separately SBDH and document body as byte arrays. The attachment is excluded.
     *
     * @param cm the container message
     * @return the original file split to two parts - SBDH and document body
     */
    public DocumentSplitterResult split(@NotNull ContainerMessage cm) throws IOException, XMLStreamException {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("No document info provided");
        }
        if (cm.getDocumentInfo().getRootNodeName() == null) {
            throw new IllegalArgumentException("Root node name is missing from the message");
        }
        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            return split(inputStream, cm.getDocumentInfo().getRootNodeName());
        }
    }

    public DocumentSplitterResult split(@NotNull InputStream inputStream, @NotNull String rootName) throws XMLStreamException, IOException {
        FastByteArrayOutputStream sbdh = new FastByteArrayOutputStream(2048); // seems like regular SBDH is inside this limit
        FastByteArrayOutputStream body = new FastByteArrayOutputStream(8192); // seems like regular file is inside this limit

        boolean collectingSbdh = false;
        boolean collectingBody = false;
        boolean putAttachment = false;

        XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
        Writer sbdhWriter = new OutputStreamWriter(sbdh);
        Writer bodyWriter = new OutputStreamWriter(body);

        ValidationError attachmentError = null;

        String name = null;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                name = start.getName().getLocalPart();

                if ("StandardBusinessDocument".equals(name)) {
                    collectingSbdh = true; collectingBody = false; putAttachment = false;
                }
                if (rootName.equals(name)) {
                    collectingSbdh = false; collectingBody = true; putAttachment = false;
                }
                if ("Attachment".equals(name)) {
                    collectingSbdh = false; collectingBody = false; putAttachment = true;
                }
            }

            if (collectingSbdh) {
                event.writeAsEncodedUnicode(sbdhWriter);
            }
            if (collectingBody) {
                event.writeAsEncodedUnicode(bodyWriter);
            }
            if (putAttachment) {
                if (event.isCharacters() && !event.asCharacters().isWhiteSpace() && "EmbeddedDocumentBinaryObject".equals(name)) {
                    attachmentError = attachmentValidator.validate(event.asCharacters().getData());
                    bodyWriter.append(MINIMAL_PDF);
                } else {
                    event.writeAsEncodedUnicode(bodyWriter);
                }
            }

            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                name = end.getName().getLocalPart();
                if (rootName.equals(name)) {
                    collectingSbdh = true; collectingBody = false; putAttachment = false;
                }
                if ("Attachment".equals(name)) {
                    collectingSbdh = false; collectingBody = true; putAttachment = false;
                }
            }
        }
        sbdhWriter.close();
        bodyWriter.close();

        return new DocumentSplitterResult(sbdh.toByteArrayUnsafe(), body.toByteArrayUnsafe(), attachmentError);
    }

}
