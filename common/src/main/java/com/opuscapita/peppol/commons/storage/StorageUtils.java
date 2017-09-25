package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class StorageUtils {

    /**
     * Returns original source from the full file path whenever possible.
     *
     * @param fileName the full file path
     * @return the file path part that contains original source information if present, else null.
     */
    @Nullable
    public static String extractOriginalSourceName(@NotNull String fileName) {
        String path = FilenameUtils.getPathNoEndSeparator(fileName);
        String suspect = StringUtils.substringAfterLast(path, File.separator);
        if (suspect.contains("_")) {
            return StringUtils.substringBeforeLast(suspect,"_");
        }
        return null;
    }

    /**
     * Creates unique file name by adding suffixes when necessary. Will not work properly if multi-threaded.
     *
     * @param directory the directory to store the file to
     * @param fileName the file name without path, if given, path will be ignored
     * @return the file name that is currently unique for this directory
     */
    @NotNull
    public static File prepareUnique(@NotNull File directory, @NotNull String fileName) {
        File result;
        int i = 0;

        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        if (StringUtils.isBlank(extension)) {
            extension = "";
        } else {
            extension = "." + extension;
        }

        fileName = baseName + extension;
        do {
            result = new File(directory, fileName);
            fileName = baseName + "_" + i++ + extension;
        } while (result.exists());

        return result;
    }

}
