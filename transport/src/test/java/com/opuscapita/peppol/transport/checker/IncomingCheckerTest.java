package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class IncomingCheckerTest {
    private RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);

    private DocumentLoader documentLoader = new DocumentLoader();

    private File tempFile;

    @Before
    public void before() throws Exception {
        try (InputStream inputStream = IncomingCheckerTest.class.getResourceAsStream("/valid/ehf.xml")) {
            tempFile = File.createTempFile("peppol_", ".tmp");
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                IOUtils.copy(inputStream, outputStream);
            }
            tempFile.deleteOnExit();
        }
    }

    @Test
    public void testBackupFile() throws Exception {
        IncomingChecker checker = new IncomingChecker(rabbitTemplate, documentLoader);
        checker.setBackup(FileUtils.getTempDirectoryPath());

        String created = checker.backupFile(tempFile);
        assertEquals(FileUtils.getTempDirectoryPath() + File.separator +
                "9908_980361330" + File.separator + "9908_923609016" + File.separator + tempFile.getName(),
                created);
    }

}