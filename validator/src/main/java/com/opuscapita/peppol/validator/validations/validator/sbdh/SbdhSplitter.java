package com.opuscapita.peppol.validator.validations.validator.sbdh;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
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
 * Reads document from disk and splits it into two parts - SBDH and document body.
 *
 * @author Sergejs.Roze
 */
@Component
public class SbdhSplitter {
    public class Result {
        private final byte[] sbdh;
        private final byte[] documentBody;

        private Result(byte[] sbdh, byte[] documentBody) {
            this.sbdh = sbdh;
            this.documentBody = documentBody;
        }

        public byte[] getSbdh() {
            return sbdh;
        }

        public byte[] getDocumentBody() {
            return documentBody;
        }
    }

    /**
     * Returns separately SBDH and document body as byte arrays. The attachment is excluded.
     *
     * @param cm the container message
     * @return the original file split to two parts - SBDH and document body
     */
    public Result split(@NotNull ContainerMessage cm) throws IOException, XMLStreamException {
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

    Result split(@NotNull InputStream inputStream, @NotNull String rootName) throws XMLStreamException, IOException {
        FastByteArrayOutputStream sbdh = new FastByteArrayOutputStream(2048); // seems like regular SBDH is inside this limit
        FastByteArrayOutputStream body = new FastByteArrayOutputStream(8192); // seems like regular file is inside this limit

        boolean collectingSbdh = false;
        boolean collectingBody = false;

        XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
        Writer sbdhWriter = new OutputStreamWriter(sbdh);
        Writer bodyWriter = new OutputStreamWriter(body);

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement start = event.asStartElement();
                String name = start.getName().getLocalPart();

                if ("StandardBusinessDocument".equals(name)) {
                    collectingSbdh = true; collectingBody = false;
                }
                if (rootName.equals(name)) {
                    collectingSbdh = false; collectingBody = true;
                }
                if ("Attachment".equals(name)) {
                    collectingSbdh = false; collectingBody = false;
                }
            }

            if (collectingSbdh) {
                event.writeAsEncodedUnicode(sbdhWriter);
            }
            if (collectingBody) {
                event.writeAsEncodedUnicode(bodyWriter);
            }

            if (event.isEndElement()) {
                EndElement end = event.asEndElement();
                String name = end.getName().getLocalPart();
                if (rootName.equals(name)) {
                    collectingSbdh = true; collectingBody = false;
                }
                if ("Attachment".equals(name)) {
                    collectingSbdh = false; collectingBody = true;
                }
            }
        }
        sbdhWriter.close();
        bodyWriter.close();

        return new Result(sbdh.toByteArrayUnsafe(), body.toByteArrayUnsafe());
    }

}
