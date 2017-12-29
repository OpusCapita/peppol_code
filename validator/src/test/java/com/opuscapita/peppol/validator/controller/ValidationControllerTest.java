package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.validator.ValidationController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FilenameFilter;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ValidationControllerTestConfig.class)
@EnableConfigurationProperties
public class ValidationControllerTest {
    private final static String[] PROFILES = { "difi", "svefaktura1" };

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ValidationController controller;

    @Autowired
    private DocumentLoader documentLoader;

    @Test
    public void validate() throws Exception {
        for (String profile : PROFILES) {
            testProfile(profile);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void testProfile(String profile) throws Exception {
        File dir = new File(new File(ValidationControllerTest.class.getResource("/test_data").getFile()), profile + "_files");
        FilenameFilter filter = (dir1, name) -> name.toLowerCase().endsWith(".xml") && name.contains("alid");
        for (String file : dir.list(filter)) {
            testFile(dir.getAbsolutePath() + File.separator + file);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void testFile(String file) throws Exception {
        ContainerMessage cm = loadDocument(file);
        try {
            cm = controller.validate(cm);
        } catch (Exception e) {
            if (cm.getFileName().contains("invalid")) {
                return;
            } else {
                fail("File " + cm.getFileName() + " has no invalid in file name but validation failed with exception:\n" + e.getMessage());
            }
        }
        if (file.toLowerCase().contains("invalid")) {
            assertTrue("Document " + file + " contains 'invalid' in the file name but the error is not found", cm.hasErrors());
        } else {
            assertFalse("Document " + file + " has no 'invalid' in the file name but failed validation: " + getErrors(cm),
                    cm.hasErrors() || cm.getDocumentInfo().getArchetype() == Archetype.INVALID);
        }
    }

    private ContainerMessage loadDocument(String file) throws Exception {
        DocumentInfo info = documentLoader.load(file, Endpoint.TEST);
        System.out.println("Document " + file + " recognized as " + info.getArchetype());
        return new ContainerMessage("meatdata", file, Endpoint.TEST).setDocumentInfo(info);
    }

    @SuppressWarnings("ConstantConditions")
    private String getErrors(ContainerMessage cm) {
        StringBuilder result = new StringBuilder();
        for (DocumentError error : cm.getDocumentInfo().getErrors()) {
            result.append("\n\t").append(error);
        }
        return result.toString();
    }

}
