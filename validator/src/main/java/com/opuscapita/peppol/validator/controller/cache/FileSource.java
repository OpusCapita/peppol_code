package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
public interface FileSource {

    File getFile(@NotNull String fileName) throws IOException;

}
