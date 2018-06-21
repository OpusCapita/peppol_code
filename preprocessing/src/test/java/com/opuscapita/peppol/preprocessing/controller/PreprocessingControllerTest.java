package com.opuscapita.peppol.preprocessing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = PreprocessingControllerTestConfig.class)
@EnableConfigurationProperties
public class PreprocessingControllerTest {
    private String files = PreprocessingControllerTest.class.getResource("/files").getPath();

    @Autowired
    private DocumentLoader documentLoader;

    @Autowired
    private PreprocessingController controller;

    @Test
    public void testItAll() throws Exception {
        String[] dirs = new File(files).list();
        if (dirs == null) {
            return;
        }
        for (String dir : dirs) {
            processDirectory(dir);
        }
    }

    private void processDirectory(String dir) throws Exception {
        System.out.println("Testing subdirectory " + dir);
        String[] testFiles = new File(files, dir).list();
        if (testFiles == null) {
            return;
        }
        for (String file : testFiles) {
            processFile(dir, file);
        }
    }

    private void processFile(String dir, String file) throws Exception {
        System.out.println("Testing file " + file);
        ContainerMessage cm = new ContainerMessage("meatdata", new File(new File(files, dir), file).getAbsolutePath(), Endpoint.TEST);

        cm = controller.process(cm);

        assertNotNull(cm.getDocumentInfo());
        assertNotNull(cm.getProcessingInfo());
        assertEquals(dir, cm.getDocumentInfo().getArchetype().toString().toLowerCase());
    }

}