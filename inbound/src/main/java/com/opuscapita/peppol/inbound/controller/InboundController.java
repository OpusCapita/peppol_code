package com.opuscapita.peppol.inbound.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
public class InboundController {
    private static final Logger logger = LoggerFactory.getLogger(InboundController.class);

    /**
     * Creates initial ContainerMessage without route.
     *
     * @param baseName the name of the file without extension.
     *                 It is expected that there will be two files starting with this name - xml (mandatory) and txt (optional)
     * @return the container message without route
     * @throws Exception something went wrong
     */
    @NotNull
    public ContainerMessage processFile(String baseName) throws Exception {
        String xml = baseName + ".xml";
        String txt = baseName + ".txt";

        if (!new File(xml).exists()) {
            throw new IllegalArgumentException("Invalid file: " + xml);
        }

        BaseDocument document = new DocumentLoader().load(xml);

        // metadata is optional
        String metadata = "";
        try {
            metadata = new String(FileUtils.readFileToByteArray(new File(txt)));
        } catch (Exception e) {
            logger.warn("Failed to read metadata file " + txt, e);
        }

        return new ContainerMessage(document, metadata, Endpoint.PEPPOL);
    }

}
