package com.opuscapita.peppol.commons.container.xml;

import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class DocumentParser {
    private final static Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private final SAXParser saxParser;
    private final DocumentTemplates templates;

    @Autowired
    public DocumentParser(@NotNull SAXParser saxParser, @NotNull DocumentTemplates templates) {
        this.saxParser = saxParser;
        this.templates = templates;
    }

    @NotNull
    public DocumentInfo parse(@NotNull InputStream inputStream, @NotNull String fileName, @NotNull Endpoint endpoint) throws Exception {
        logger.warn("DocumentParserHandler created!");
        DocumentParserHandler handler = new DocumentParserHandler(fileName, templates, endpoint);

        try {
            saxParser.parse(inputStream, handler);
        } catch (Exception e) {
            logger.warn("Failed to parse file " + fileName, e);
            DocumentInfo result = new DocumentInfo();
            result.setArchetype(Archetype.INVALID);
            result.getErrors().add(new DocumentError(endpoint, "Failed to parse file " + fileName + ": " + e.getMessage()));
            return result;
        }

        return handler.getResult();
    }

}
