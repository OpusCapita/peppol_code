package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.xml.DocumentHeaderParser;
import com.opuscapita.peppol.commons.container.xml.DocumentParser;
import no.difi.vefa.peppol.common.model.Header;
import no.difi.vefa.peppol.sbdh.SbdReader;
import no.difi.vefa.peppol.sbdh.lang.SbdhException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private final DocumentParser documentParser;
    private final DocumentHeaderParser headerParser;
    private final boolean shouldFailOnInconsistency;

    @Autowired
    public DocumentLoader(@NotNull DocumentParser documentParser, @Nullable DocumentHeaderParser headerParser,
                          @Value("${peppol.common.consistency_check_enabled}") boolean shouldFailOnInconsistency) {
        this.headerParser = headerParser;
        this.documentParser = documentParser;
        this.shouldFailOnInconsistency = shouldFailOnInconsistency;
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
        String shortFileName = new File(fileName).getName();
        return documentParser.parse(inputStream, shortFileName, endpoint, shouldFailOnInconsistency);
    }

    public Header parseHeader(@NotNull String fileName) {
        return parseSBDHeader(fileName);
    }

    private Header parseSBDHeader(@NotNull String fileName) {
        try {
            InputStream inputStream = new FileInputStream(new File(fileName));
            SbdReader sbdReader = SbdReader.newInstance(inputStream);
            return sbdReader.getHeader();

        } catch (Exception e) {
            if (e instanceof SbdhException) {
                return extractHeader(fileName);
            }
            e.printStackTrace();
        }
        logger.warn("Couldn't extract header from file: " + fileName);
        return null;
    }

    private Header extractHeader(@NotNull String fileName) {
        try {
            InputStream inputStream = new FileInputStream(new File(fileName));
            return headerParser.parse(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.warn("Couldn't extract header from file: " + fileName);
        return null;
    }
}