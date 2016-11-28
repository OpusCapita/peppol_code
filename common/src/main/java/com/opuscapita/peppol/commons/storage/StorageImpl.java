package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergejs.Roze
 */
@Component
public class StorageImpl implements Storage {
    @Value("${peppol.storage.short}")
    private String shortTerm;

    @Value("${peppol.storage.long}")
    private String longTerm;

    @NotNull
    @Override
    public String storeTemporary(@NotNull InputStream stream, @NotNull String fileName) throws IOException {
        File dir = createDailyDirectory();
        File result = new File(dir, fileName);

        IOUtils.copy(stream, new FileOutputStream(result));

        return result.getAbsolutePath();
    }

    @NotNull
    @Override
    public String moveToTemporary(@NotNull File source) throws IOException {
        File dir = createDailyDirectory();

        File result = StorageUtils.prepareUnique(dir, source.getName());
        FileUtils.moveFile(source, result);
        if (!result.exists()) {
            throw new IOException("Failed to move file " + source + " to " + result);
        }

        return result.getAbsolutePath();
    }

    private File createDailyDirectory() throws IOException {
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File dir = new File(shortTerm, date);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dir);
            }
        }

        return dir;
    }

    @NotNull
    @Override
    public String moveToLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull String fileName) throws IOException {
        return moveToLongTerm(senderId, recipientId, new File(fileName));
    }

    @NotNull
    @Override
    public String moveToLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull File file) throws IOException {
        senderId = normalizeFilename(senderId);
        recipientId = normalizeFilename(recipientId);
        String date = new SimpleDateFormat("yyyymmdd").format(new Date());

        File dir = new File(longTerm + File.separator + senderId + File.separator + recipientId + File.separator + date);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dir);
            }
        }

        File result = StorageUtils.prepareUnique(dir, file.getName());
        FileUtils.moveFile(file, result);
        if (!result.exists()) {
            throw new IOException("Failed to move file " + file + " to " + result);
        }

        return result.getAbsolutePath();
    }

    // for use without Spring context, sorry
    public Storage setShortTermDirectory(@NotNull String shortTerm) {
        this.shortTerm = shortTerm;
        return this;
    }

    private static String normalizeFilename(String s) {
        s = StringUtils.isBlank(s) ? "unknown" : s;
        return s.replaceAll("[^a-zA-Z0-9.-]", "_");
    }


}
