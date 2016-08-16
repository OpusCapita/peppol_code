package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

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
    public InputStream read(@NotNull String fileId) throws IOException {
        return new FileInputStream(fileId);
    }

    @NotNull
    @Override
    public String storeTemporary(@NotNull byte[] bytes) throws IOException {
        return storeTemporary(bytes, UUID.randomUUID().toString() + ".xml");
    }

    @NotNull
    @Override
    public String storeTemporary(@NotNull byte[] bytes, @NotNull String baseName) throws IOException {
        String fileName = FilenameUtils.getName(baseName);
        if (StringUtils.isBlank(fileName)) {
            return storeTemporary(bytes);
        }

        String filePath = shortTerm + File.separator + fileName;
        FileUtils.writeByteArrayToFile(new File(filePath), bytes); // creates directories if needed

        return filePath;
    }

    @NotNull
    @Override
    public String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull byte[] bytes) throws IOException {
        return storeLongterm(senderId, recipientId, bytes, UUID.randomUUID().toString());
    }

    @NotNull
    @Override
    public String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull byte[] bytes, @NotNull String baseName)
            throws IOException {
        String fileName = FilenameUtils.getName(baseName);
        if (StringUtils.isBlank(fileName)) {
            return storeLongterm(senderId, recipientId, bytes);
        }
        senderId = StringUtils.isBlank(senderId) ? "unknown" : senderId;
        recipientId = StringUtils.isBlank(recipientId) ? "unknown" : recipientId;

        String filePath = longTerm + File.separator + senderId + File.separator + recipientId + File.separator + baseName;
        FileUtils.writeByteArrayToFile(new File(filePath), bytes);

        return filePath;
    }

    protected void setShortTerm(String shortTerm) {
        this.shortTerm = shortTerm;
    }

    protected void setLongTerm(String longTerm) {
        this.longTerm = longTerm;
    }

}
