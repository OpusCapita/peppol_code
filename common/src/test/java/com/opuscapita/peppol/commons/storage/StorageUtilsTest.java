package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class StorageUtilsTest {

    @Test
    public void prepareUnique() throws Exception {
        File temp = File.createTempFile("storage_utils_test_", ".delete");
        temp.deleteOnExit();

        String directory = temp.getParent();
        String fileName = temp.getName();

        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        String expected = directory + File.separator + baseName + "_0." + extension;

        if (new File(expected).exists()) {
            System.out.println("Deleting existing file " + expected + " assuming it is ours");
            new File(expected).delete();
        }

        File next = StorageUtils.prepareUnique(new File(directory), fileName);

        assertNotNull(next);
        assertEquals(expected, next.getAbsolutePath());
    }

    @Test
    public void prepareUniqueNoExtension() throws Exception {
        File temp = File.createTempFile("storage_utils_test_", "delete");
        temp.deleteOnExit();

        String directory = temp.getParent();
        String fileName = temp.getName();

        String baseName = FilenameUtils.getBaseName(fileName);
        String expected = directory + File.separator + baseName + "_0";

        if (new File(expected).exists()) {
            System.out.println("Deleting existing file " + expected + " assuming it is ours");
            new File(expected).delete();
        }

        File next = StorageUtils.prepareUnique(new File(directory), fileName);

        assertNotNull(next);
        assertEquals(expected, next.getAbsolutePath());
    }

}
