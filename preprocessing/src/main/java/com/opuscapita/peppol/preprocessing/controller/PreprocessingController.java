package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.storage.Storage;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Parses input document and moves file from temporary to long-term storage.
 *
 * @author Sergejs.Roze
 */
@Component
public class PreprocessingController {
    private static final Logger logger = LoggerFactory.getLogger(PreprocessingController.class);
    private final DocumentLoader documentLoader;
    private final Storage storage;

    @Autowired
    public PreprocessingController(@NotNull DocumentLoader documentLoader, @NotNull Storage storage) {
        this.documentLoader = documentLoader;
        this.storage = storage;
    }

    /**
     * Parses input container message and creates a new one with a document inside.
     *
     * @param cm the input container message without document.
     * @return the container message without route
     * @throws Exception something went wrong
     */
    @NotNull
    public ContainerMessage process(@NotNull ContainerMessage cm) throws Exception {
        if (StringUtils.isBlank(cm.getFileName())) {
            throw new IllegalArgumentException("File name is empty in received message");
        }

        try {
            logger.info("Parsing file: " + cm.getFileName());
            BaseDocument document = documentLoader.load(cm.getFileName());

            String longTerm = storage.moveToLongTerm(document.getSenderId(), document.getRecipientId(), cm.getFileName());
            logger.info("Input file " + cm.getFileName() + " moved to " + longTerm);

            return cm.setBaseDocument(document).setFileName(longTerm);
        } catch (Exception e) {
            logger.warn("Failed to process file: " + e.getMessage());
            return cm.setBaseDocument(new InvalidDocument("Failed to process input file", e));
        }
    }

}
