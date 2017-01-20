package com.opuscapita.peppol.email.sender;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;

import static org.easymock.EasyMock.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class DirectoryCheckerTest {
    private static File directory;
    private EmailSender emailSender = mock(EmailSender.class);

    @BeforeClass
    public static void beforeClass() throws Exception {
        File tempDirectory = FileUtils.getTempDirectory();
        directory = new File(tempDirectory, "unit-test");
        directory.deleteOnExit();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void moveOrAppendNoFiles() throws Exception {
        File source = File.createTempFile("unittest-", ".delete.me");
        String original = source.getName();
        new FileOutputStream(source).write("ABC".getBytes());
        source.deleteOnExit();

        new DirectoryChecker(emailSender).moveOrAppend(source, directory);

        File result = new File(directory, original);
        assertTrue(result.exists());
        List<String> lines = Files.readAllLines(result.toPath());
        assertEquals(1, lines.size());
        assertEquals("ABC", lines.get(0));
        result.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void moveOrAppendFileExists() throws Exception {
        File source = File.createTempFile("unittest-", ".delete.me");
        String original = source.getName();
        new FileOutputStream(source).write("ABC\nDEF".getBytes());
        source.deleteOnExit();

        File result = new File(directory, original);
        new FileOutputStream(result).write("123\n456".getBytes());
        assertTrue(result.exists());

        new DirectoryChecker(emailSender).moveOrAppend(source, directory);

        assertTrue(result.exists());
        List<String> lines = Files.readAllLines(result.toPath());
        assertEquals(5, lines.size());
        assertEquals("123", lines.get(0));
        assertEquals("456", lines.get(1));
        assertEquals("", lines.get(2));
        assertEquals("ABC", lines.get(3));
        assertEquals("DEF", lines.get(4));
        result.delete();
    }

}