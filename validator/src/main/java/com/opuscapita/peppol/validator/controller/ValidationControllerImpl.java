package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.validator.controller.attachment.DocumentSplitter;
import com.opuscapita.peppol.validator.controller.attachment.DocumentSplitterResult;
import com.opuscapita.peppol.validator.controller.body.BodyValidator;
import com.opuscapita.peppol.validator.controller.xsd.HeaderValidator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
@ConditionalOnProperty(name = "peppol.validator.difi.enabled", havingValue = "false")
public class ValidationControllerImpl implements com.opuscapita.peppol.validator.ValidationController {

    private static final Logger logger = LoggerFactory.getLogger(ValidationControllerImpl.class);

    private final DocumentSplitter splitter;
    private final HeaderValidator headerValidator;
    private final BodyValidator bodyValidator;

    @Autowired
    public ValidationControllerImpl(@NotNull DocumentSplitter splitter, @NotNull HeaderValidator headerValidator,
                                    @NotNull BodyValidator bodyValidator) {
        this.splitter = splitter;
        this.headerValidator = headerValidator;
        this.bodyValidator = bodyValidator;
        logger.info("Using MESE validation controller");
    }

    @NotNull
    public ContainerMessage validate(@NotNull ContainerMessage cm, @NotNull Endpoint endpoint)
            throws IOException, XMLStreamException, TransformerException, SAXException, ParserConfigurationException, TimeoutException {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document info is missing from message " + cm.getFileName());
        }
        Archetype at = cm.getDocumentInfo().getArchetype();
        if (cm.hasErrors() || at == Archetype.INVALID || at == Archetype.UNRECOGNIZED) {
            return cm;
        }

        checkForDocumentRoot(cm);
        DocumentSplitterResult parts = splitter.split(cm);

        cm = headerValidator.validate(parts.getSbdh(), cm);

        //Svefaktura1 ObjectEnvelope parsing for the case when double SBDH wrap is applied
        if (cm.getDocumentInfo().getDocumentType().equals("InvoiceWithObjectEnvelope")) {
            Map<String, DocumentError> knownErrorsToBeParoled = new HashMap<String, DocumentError>() {
                {
                    put("Invalid content was found starting with element 'sh:StandardBusinessDocument'", null);
                    put("Cannot find the declaration of element 'Invoice'", null);
                    put("Cannot find the declaration of element 'ObjectEnvelope'", null);
                }
            };

            cm = validateForDocumentRoot(cm, endpoint, "Invoice");
            cm = validateForDocumentRoot(cm, endpoint, "ObjectEnvelope");

            Iterator<DocumentError> documentErrorIterator = cm.getDocumentInfo().getErrors().iterator();
            while (documentErrorIterator.hasNext()) {
                DocumentError error = documentErrorIterator.next();
                knownErrorsToBeParoled.keySet().forEach(knownError -> {
                    if(error.getMessage().contains(knownError)) {
                        knownErrorsToBeParoled.put(knownError, error);
                        documentErrorIterator.remove();
                    }
                });
            }
        } else {
            cm = bodyValidator.validate(parts.getDocumentBody(), cm, endpoint);
            if (parts.getAttachmentError() != null) {
                cm.addError(parts.getAttachmentError().toDocumentError(endpoint));
            }
        }


        return cm;
    }

    @NotNull
    private ContainerMessage validateForDocumentRoot(@NotNull ContainerMessage cm, @NotNull Endpoint endpoint, String rootName)
            throws IOException, XMLStreamException, ParserConfigurationException, SAXException, TransformerException, TimeoutException {
        DocumentSplitterResult parts;
        try (InputStream inputStream = new FileInputStream(cm.getFileName())) {
            parts = splitter.split(inputStream, rootName);
            cm = bodyValidator.validate(parts.getDocumentBody(), cm, endpoint);
        }
        return cm;
    }

    private void checkForDocumentRoot(@NotNull ContainerMessage cm) {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("No document info provided");
        }
        if (cm.getDocumentInfo().getRootNodeName() == null) {
            throw new IllegalArgumentException("Root node name is missing from the message");
        }
    }
}
