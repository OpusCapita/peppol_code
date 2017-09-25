package com.opuscapita.peppol.commons.storage;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

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

    @Test
    public void testExtractOriginalSource() throws Exception {
        assertEquals("endpoint_name",
                StorageUtils.extractOriginalSourceName("/dev/null/not_this/endpoint_name_20170922/file_name.xml"));
        assertNull(StorageUtils.extractOriginalSourceName("/dev/null/not_this/20170922/file_name.xml"));
    }
}
