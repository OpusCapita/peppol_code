package com.opuscapita.peppol.commons.storage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
public interface Storage {

    /**
     * Stores file to temporary storage.
     *
     * @param stream the input stream
     * @param fileName the name of the file to store
     * @return the file ID
     */
    @NotNull
    String storeTemporary(@NotNull InputStream stream, @NotNull String fileName) throws IOException;

    /**
     * Stores file to temporary storage.
     *
     * @param source the file to store
     * @return the file ID
     */
    @NotNull
    String storeTemporary(@NotNull File source) throws IOException;

    /**
     * Stores data to long-term storage.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param fileName the name of the file to store
     * @return the file ID
     */
    @NotNull
    String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull String fileName) throws IOException;

    /**
     * Stores data to long-term storage.
     *
     * @param senderId the sender ID, use empty line when missing
     * @param recipientId the recipient ID, use empty line when missing
     * @param input the file to store
     * @return the file ID
     */
    @NotNull
    String storeLongterm(@NotNull String senderId, @NotNull String recipientId, @NotNull File input) throws IOException;

}
