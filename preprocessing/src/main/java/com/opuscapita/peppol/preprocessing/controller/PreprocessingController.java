package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class PreprocessingController {
    private static final Logger logger = LoggerFactory.getLogger(PreprocessingController.class);
    private final DocumentLoader documentLoader;

    @Autowired
    public PreprocessingController(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }

    /**
     * Parses input container message and creates a new one with a document inside.
     *
     * @param input the input container message without document.
     * @return the container message without route
     * @throws Exception something went wrong
     */
    @NotNull
    public ContainerMessage process(ContainerMessage input) throws Exception {
        logger.info("Parsing file: " + input.getFileName());
        BaseDocument document = documentLoader.load(input.getFileName());

        return new ContainerMessage(document, input.getSourceMetadata(), input.getSource(), input.getFileName(), null);
    }

}
