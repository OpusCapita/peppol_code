package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
public class DocumentLoader {
    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);

    private final DocumentParser parser;

    @Autowired
    public DocumentLoader(@NotNull DocumentParser parser) {
        this.parser = parser;
    }

    @NotNull
    public DocumentInfo load(@NotNull String fileName, @NotNull Endpoint endpoint) throws Exception {
        return load(new File(fileName), endpoint);
    }

    @NotNull
    public DocumentInfo load(@NotNull File file, @NotNull Endpoint endpoint) throws Exception {
        try (InputStream inputStream = new FileInputStream(file)) {
            return load(inputStream, file.getAbsolutePath(), endpoint);
        }
    }

    @NotNull
    public DocumentInfo load(@NotNull InputStream inputStream, @NotNull String fileName, @NotNull Endpoint endpoint) throws Exception {
        logger.info("Start parsing file: " + fileName);
        return parser.parse(inputStream, fileName, endpoint);
    }

}
