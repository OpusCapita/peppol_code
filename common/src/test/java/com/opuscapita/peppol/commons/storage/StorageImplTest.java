package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Sergejs.Roze
 */
public class StorageImplTest {
    private static String tempDir;
    private static String backupDir;

    @BeforeClass
    public static void setUp() throws IOException {
        tempDir = Files.createTempDirectory("storage-test-").toString();
        backupDir = Files.createTempDirectory("storage-test-").toString();
        FileUtils.forceDeleteOnExit(new File(tempDir));
        FileUtils.forceDeleteOnExit(new File(backupDir));
    }

    @AfterClass
    public static void finish() throws IOException {
        FileUtils.deleteDirectory(new File(tempDir));
        FileUtils.deleteDirectory(new File(backupDir));
    }

    @Test
    public void testShortTerm() throws IOException {
        // create and check simple file
        StorageImpl storage = new StorageImpl();
        storage.setShortTermDirectory(tempDir);

        FastByteArrayOutputStream bytes = new FastByteArrayOutputStream();
        bytes.write("test".getBytes());

        String result = storage.storeTemporary(bytes.getInputStream(), "test.file", backupDir);
        assertTrue(new File(result).exists());
        assertTrue(new File(backupDir, "test.file").exists());

        // try to use this file as a directory
        storage.setShortTermDirectory(result);
        try {
            storage.storeTemporary(bytes.getInputStream(), "test.file.what", backupDir);
            fail("We've just created a directory inside a file");
        } catch (IOException expected) {}
    }

}