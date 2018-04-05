package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
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
import java.io.IOException;

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
            throws IOException, XMLStreamException, TransformerException, SAXException, ParserConfigurationException {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document info is missing from message " + cm.getFileName());
        }
        Archetype at = cm.getDocumentInfo().getArchetype();
        if (cm.hasErrors() || at == Archetype.INVALID || at == Archetype.UNRECOGNIZED) {
            return cm;
        }

        DocumentSplitterResult parts = splitter.split(cm);

        cm = headerValidator.validate(parts.getSbdh(), cm);
        cm = bodyValidator.validate(parts.getDocumentBody(), cm, endpoint);
        if (parts.getAttachmentError() != null) {
            cm.addError(parts.getAttachmentError().toDocumentError(endpoint));
        }

        return cm;
    }
}
