package com.opuscapita.peppol.commons.container.document;

import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergejs.Roze
 */
@Component
public class DocumentLoader {
    private static final String TYPES_PACKAGE = "com.opuscapita.peppol.commons.container.document.impl";
    private static Set<BaseDocument> examples;
    private static final Logger logger = LoggerFactory.getLogger(DocumentLoader.class);

    static {
        try {
            examples = reloadDocumentTypes(TYPES_PACKAGE);
        } catch (Exception e) {
            logger.error("Unable to create list of known document types", e);
        }
    }

    @NotNull
    public BaseDocument load(@NotNull String fileName) throws IOException {
        return load(new File(fileName));
    }

    @NotNull
    public BaseDocument load(@NotNull File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return load(inputStream, file.getAbsolutePath());
        }
    }

    @NotNull
    public BaseDocument load(@NotNull InputStream inputStream, @NotNull String fileName) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);

        Document document;
        try {
            document = DocumentUtils.getDocument(bytes);
        } catch (Exception e) {
            logger.warn("Unable to parse document " + fileName, e);
            return new InvalidDocument("Unable to parse document", e);
        }
        return select(document);
    }

    @NotNull
    private BaseDocument select(Document document) {
        BaseDocument result;
        for (BaseDocument example : examples) {
            if (example.recognize(document)) {
                try {
                    result = example.getClass().newInstance();
                    result.setDocument(document);
                    boolean success = result.fillFields();
                    if (success) {
                        return result;
                    } else {
                        return new InvalidDocument("Failed to read data from the document");
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.warn("Unable to create document object", e);
                    return new InvalidDocument("Unable to create document object", e);
                }
            }
        }
        return new InvalidDocument("Unable to determine document type", null);
    }

    static Set<BaseDocument> reloadDocumentTypes(String pakkage)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        logger.info("Loading set of known document types from " + pakkage);
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(PeppolDocument.class));
        Set<BeanDefinition> found =
                provider.findCandidateComponents(pakkage);
        logger.info("Found " + found.size() + " document type(s) to load");

        Set<BaseDocument> result = new HashSet<>(found.size());
        for (BeanDefinition bd : found) {
            result.add((BaseDocument) Class.forName(bd.getBeanClassName()).newInstance());
        }
        return result;
    }
}
