package com.opuscapita.peppol.commons.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
public interface Storage {

    /**
     * Stores stream to temporary storage.
     *
     * @param stream the input stream
     * @param fileName the name of the file to store
     * @return the file ID
     * @throws IOException something went wrong
     */
    @NotNull
    String storeTemporary(@NotNull InputStream stream, @NotNull String fileName) throws IOException;

    /**
     * Stores stream to temporary storage with additional backup.
     *
     * @param stream the input stream
     * @param fileName the name of the file to store
     * @param backupDir the backup directory location, will be ignored on null value
     * @return the file ID
     * @throws IOException something went wrong
     */
    @SuppressWarnings("SameParameterValue")
    @NotNull
    String storeTemporary(@NotNull InputStream stream, @NotNull String fileName, @Nullable String backupDir) throws IOException;

    /**
     * Moves file to temporary storage.
     *
     * @param source the file to store
     * @return the file ID
     */
    @SuppressWarnings("unused")
    @NotNull
    String moveToTemporary(@NotNull File source) throws IOException;

    /**
     * Moves file to temporary storage and creates file backup.
     *
     * @param source the file to store
     * @param backupDir the backup directory location, will be ignored on null value
     * @return the file ID
     * @throws IOException something went wrong
     */
    @NotNull
    String moveToTemporary(@NotNull File source, @Nullable String backupDir) throws IOException;

    /**
     * Stores data to long-term storage.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param source the originator of the message, like transport name
     * @param fileName the name of the file to store
     * @return the file ID
     */
    @NotNull
    String moveToLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull String source, @NotNull String fileName) throws IOException;

    /**
     * Stores data to long-term storage.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param source the originator of the message, like transport name
     * @param input the file to store
     * @return the file ID
     */
    @NotNull
    String moveToLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull String source, @NotNull File input) throws IOException;

    /**
     * Stores input stream data in a long term storage with a desired file name.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param source the originator of the message, like transport name
     * @param fileName the desired name of the file
     * @param inputStream the data to store
     * @return the file ID
     */
    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    String storeLongTerm(@NotNull String senderId, @NotNull String recipientId, @NotNull String source, @NotNull String fileName, @NotNull InputStream inputStream) throws IOException;

}
