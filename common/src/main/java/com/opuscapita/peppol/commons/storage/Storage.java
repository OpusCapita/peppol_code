package com.opuscapita.peppol.commons.storage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
public interface Storage {

    /**
     * Reads file from storage. There is no difference between temporary and long-time storage when reading.
     *
     * @param fileId the file ID
     * @return the file as stream
     */
    @NotNull
    InputStream read(@NotNull String fileId) throws IOException;

    /**
     * Stores data to temporary storage. Uses freshly generated UUID as a file name.
     *
     * @param bytes the data to store
     * @return the file ID
     */
    @NotNull
    String storeTemporary(@NotNull byte[] bytes) throws IOException;

    /**
     * Stores data to temporary storage using given base file name.
     *
     * @param bytes the data to store
     * @param baseName the base name of the file, directories will be ignored
     * @return the file ID
     */
    @NotNull
    String storeTemporary(@NotNull byte[] bytes, @NotNull String baseName) throws IOException;

    /**
     * Stores data to long-term storage. Uses freshly generated UUID as a file name.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param bytes the data to store
     * @return the file ID
     */
    @NotNull
    String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull byte[] bytes) throws IOException;

    /**
     * Stores data to long-term storage. Uses given file name.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param bytes the data to store
     * @param baseName the base name of the file, directories will be ignored
     * @return the file ID
     */
    @NotNull
    String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull byte[] bytes, @NotNull String baseName)
            throws IOException;
}
