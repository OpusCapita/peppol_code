package com.opuscapita.peppol.commons.storage;

import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StorageImpl extends ValuesChecker implements Storage {
    private static final Logger logger = LoggerFactory.getLogger(StorageImpl.class);

    @FileMustExist
    @Value("${peppol.storage.short}")
    private String shortTerm;

    @FileMustExist
    @Value("${peppol.storage.long}")
    private String longTerm;

    private static String normalizeFilename(String s) {
        s = StringUtils.isBlank(s) ? "unknown" : s;
        return s.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private void check(File file) throws IOException {
        if (!file.canRead()) {
            throw new IOException("Unable to read file " + file.getAbsolutePath());
        }
        if (file.length() == 0) {
            throw new IOException("File is empty: " + file.getAbsolutePath());
        }
    }

    @NotNull
    @Override
    public String storeTemporary(@NotNull InputStream stream, @NotNull String fileName) throws IOException {
        return storeTemporary(stream, fileName, null);
    }

    @NotNull
    @Override
    public String storeTemporary(@NotNull InputStream stream, @NotNull String fileName, @Nullable String backupDir) throws IOException {
        File dir = createDailyDirectory();
        File result = new File(dir, fileName);

        FileUtils.copyInputStreamToFile(stream, result);
        if (!result.exists() || result.length() == 0) {
            throw new IOException("Failed to copy file " + fileName + " to " + result);
        }

        if (StringUtils.isNotBlank(backupDir)) {
            FileUtils.copyFileToDirectory(result, new File(backupDir));
        }

        return result.getAbsolutePath();
    }

    @NotNull
    @Override
    public String moveToTemporary(@NotNull File source) throws IOException {
        return moveToTemporary(source, null);
    }

    @NotNull
    @Override
    public String moveToTemporary(@NotNull File source, @Nullable String backupDir) throws IOException {
        check(source);
        File dir = createDailyDirectory();

        File result = StorageUtils.prepareUnique(dir, source.getName());
        FileUtils.moveFile(source, result);
        if (!result.exists() || result.length() == 0) {
            throw new IOException("Failed to move file " + source + " to " + result);
        }

        if (StringUtils.isNotBlank(backupDir)) {
            FileUtils.copyFileToDirectory(result, new File(backupDir));
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
        check(file);
        if (file.getAbsolutePath().startsWith(longTerm)) {
            logger.info("File already in long term storage, not moving: " + file.getAbsolutePath());
            return file.getAbsolutePath();
        }
        File dir = prepareDirectory(senderId, recipientId);

        File result = StorageUtils.prepareUnique(dir, file.getName());
        FileUtils.moveFile(file, result);
        if (!result.exists() || result.length() == 0) {
            throw new IOException("Failed to move file " + file + " to " + result);
        }

        return result.getAbsolutePath();
    }

    @NotNull
    @Override
    public String storeLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull String fileName, @NotNull InputStream inputStream) throws IOException {
        File dir = prepareDirectory(senderId, recipientId);

        File result = StorageUtils.prepareUnique(dir, FilenameUtils.getName(fileName));
        IOUtils.copy(inputStream, new FileOutputStream(result));

        return result.getAbsolutePath();
    }

    private File prepareDirectory(@NotNull String senderId, @NotNull String recipientId) throws IOException {
        senderId = normalizeFilename(senderId);
        recipientId = normalizeFilename(recipientId);
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());

        File dir = new File(longTerm + File.separator + senderId + File.separator + recipientId + File.separator + date);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dir);
            }
        }

        return dir;
    }

    // for use without Spring context, sorry
    public Storage setShortTermDirectory(@NotNull String shortTerm) {
        this.shortTerm = shortTerm;
        return this;
    }

}
