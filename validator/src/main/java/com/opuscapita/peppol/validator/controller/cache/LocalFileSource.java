package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
public class LocalFileSource implements FileSource {
    @Value("${peppol.validator.rules.directory}")
    private String directory;

    @Override
    public File getFile(@NotNull String fileName) {
        return new File(directory, fileName);
    }

}
