package com.opuscapita.peppol.commons.container;

import org.jetbrains.annotations.NotNull;
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

    @NotNull
    public ContainerMessage create(@NotNull String fileName) throws IOException {
        return create(new File(fileName));
    }

    @NotNull
    public ContainerMessage create(@NotNull File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return create(is);
        }
    }

    @NotNull
    private ContainerMessage create(@NotNull InputStream inputStream) {
        return null; // TODO
    }

}
