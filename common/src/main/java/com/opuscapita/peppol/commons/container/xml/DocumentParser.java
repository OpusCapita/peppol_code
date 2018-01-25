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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
//@Lazy
public class DocumentParser {
    private final static Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private final SAXParserFactory saxParserFactory;
    private final DocumentTemplates templates;
    private ThreadLocal<SAXParser> saxParser = new ThreadLocal<>();

    @Autowired
    public DocumentParser(@NotNull SAXParserFactory saxParserFactory, @NotNull DocumentTemplates templates) {
        this.saxParserFactory = saxParserFactory;
        this.templates = templates;
    }

    @NotNull
    public DocumentInfo parse(@NotNull InputStream inputStream, @NotNull String fileName, @NotNull Endpoint endpoint, boolean shouldFailOnInconsistency) throws Exception {
        DocumentParserHandler handler = new DocumentParserHandler(fileName, templates, endpoint, shouldFailOnInconsistency);
        try {
            getSAXParser().parse(inputStream, handler);
        } catch (Exception e) {
            logger.warn("Failed to parse file " + fileName, e);
            DocumentInfo result = new DocumentInfo();
            result.setArchetype(Archetype.INVALID);
            result.getErrors().add(new DocumentError(endpoint, "Failed to parse file " + fileName + ": " + e.getMessage()));
            return result;
        }

        return handler.getResult();
    }

    protected SAXParser getSAXParser() throws ParserConfigurationException, SAXException {
        if (saxParser.get() == null) {
            saxParser.set(saxParserFactory.newSAXParser());
        }
        return saxParser.get();
    }

}
