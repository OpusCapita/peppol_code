package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.validator.controller.body.BodyValidator;
import com.opuscapita.peppol.validator.controller.util.DocumentSplitter;
import com.opuscapita.peppol.validator.controller.xsd.HeaderValidator;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
//@Component
public class ValidationController {
    private final DocumentSplitter splitter;
    private final HeaderValidator headerValidator;
    private final BodyValidator bodyValidator;

    //@Autowired
    public ValidationController(@NotNull DocumentSplitter splitter, @NotNull HeaderValidator headerValidator, @NotNull BodyValidator bodyValidator) {
        this.splitter = splitter;
        this.headerValidator = headerValidator;
        this.bodyValidator = bodyValidator;
    }

    @NotNull
    public ContainerMessage validate(@NotNull ContainerMessage cm) throws IOException, XMLStreamException, TransformerException, SAXException, ParserConfigurationException {
        if (cm.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document info is missing from message " + cm.getFileName());
        }
        Archetype at = cm.getDocumentInfo().getArchetype();
        if (cm.hasErrors() || at == Archetype.INVALID || at == Archetype.UNRECOGNIZED) {
            return cm;
        }

        DocumentSplitter.Result parts = splitter.split(cm);

        cm = headerValidator.validate(parts.getSbdh(), cm);
        cm = bodyValidator.validate(parts.getDocumentBody(), cm);

        return cm;
    }
}
