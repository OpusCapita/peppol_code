package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main entry point for container creation. Loads message from a given source and
 * sets all required fields.
 *
 * @author Sergejs.Roze
 */
@Component
public class ContainerMessageFactory {
    private final DocumentLoader documentLoader;

    @Autowired
    public ContainerMessageFactory(DocumentLoader documentLoader) {
        this.documentLoader = documentLoader;
    }

    @NotNull
    public ContainerMessage create(@NotNull String fileName) throws IOException {
        return create(new File(fileName));
    }

    @NotNull
    public ContainerMessage create(@NotNull File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return create(is, file.getAbsolutePath());
        }
    }

    @NotNull
    public ContainerMessage create(@NotNull InputStream inputStream, @NotNull String fileName) throws IOException {
        BaseDocument baseDocument = documentLoader.load(inputStream, fileName);
        return loadRoute(baseDocument);
    }

    @NotNull
    private ContainerMessage loadRoute(@NotNull BaseDocument baseDocument) {
        return null;
    }
}

