package com.opuscapita.peppol.commons.container.xml;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sergejs.Roze
 */
@Component
public class DocumentTemplates {
    private final static Logger logger = LoggerFactory.getLogger(DocumentTemplates.class);

    private final List<DocumentTemplate> templates = new ArrayList<>();
    private final ErrorHandler errorHandler;
    private final Gson gson;

    public DocumentTemplates(@Nullable ErrorHandler errorHandler, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.gson = gson;

        // FIXME this is really bad hack
        File test = new File("/etc/peppol2.0/preprocessing/document_types");
        if (test.exists()) {
            loadRealDir("/etc/peppol2.0/preprocessing/document_types");
        } else {
            loadResourceDir("/document_types");
        }

        logger.info("Read " + templates.size() + " document format templates");
    }

    private void loadRealDir(@NotNull String dir) {
        logger.info("Reading document templates from directory " + dir + File.separator);
        String[] files = new File(dir).list();
        if (files == null) {
            return;
        }

        for (String file : files) {
            if (new File(file).isDirectory()) {
                loadRealDir(dir + File.separator + file);
            } else {
                if (file.endsWith(".json")) {
                    loadTemplateFromFile(dir + File.separator + file);
                }
            }
        }
    }

    private void loadTemplateFromFile(@NotNull String fileName) {
        try (Reader reader = new BufferedReader(new FileReader(fileName))) {
            templates.add(gson.fromJson(reader, DocumentTemplate.class));
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.reportWithoutContainerMessage(null, e,
                        "Failed to read document templates from file " + fileName, null, null);
            }
        }
    }

    private void loadResourceDir(@NotNull String dir) {
        logger.info("Reading document templates as resources from " + dir + File.separator);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(DocumentTemplates.class.getResourceAsStream(dir)))) {
            String fileName = reader.readLine();
            while (fileName != null) {
                if (fileName.endsWith(".json")) {
                    loadTemplate(dir + File.separator + fileName);
                } else {
                    loadResourceDir(dir + File.separator + fileName);
                }
                fileName = reader.readLine();
            }
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.reportWithoutContainerMessage(
                        null, e, "Failed to read document templates from " + dir, null, null);
            }
        }
    }

    private void loadTemplate(@NotNull String fileName) throws IOException {
        logger.debug("Reading document template from " + fileName);
        try (Reader reader = new InputStreamReader(DocumentTemplates.class.getResourceAsStream(fileName))) {
            templates.add(gson.fromJson(reader, DocumentTemplate.class));
        }
    }

    public List<DocumentTemplate> getTemplates() {
        return Collections.unmodifiableList(templates);
    }
}
