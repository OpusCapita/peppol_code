package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.validator.ValidationController;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ValidationControllerTestConfig.class)
@EnableConfigurationProperties
public class ValidationControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ValidationController controller;

    @Autowired
    private DocumentLoader documentLoader;

    @Test
    public void goThroughTestMaterials() throws Exception {
        File testFiles = new File(ValidationControllerTest.class.getResource("/test-materials").getFile());
        processDirectory(testFiles);
    }

    @SuppressWarnings("ConstantConditions")
    private void processDirectory(@NotNull File dir) throws Exception {
        List<String> list = Arrays.asList(dir.list());
        if (list.contains("ignore")) {
            return;
        }

        for (String fileName : list) {
            File file = new File(dir, fileName);
            if (file.isDirectory()) {
                processDirectory(file);
            } else {
                processFile(file);
            }
        }
    }

    @Test
    @Ignore("Debug only")
    public void testSingleFile() throws Exception {
        File file = new File("C:\\Users\\ibilge\\IdeaProjects\\peppol_code\\validator\\src\\test\\resources\\test-materials\\cases\\eventing_valid.xml");
        Assert.notNull(file);

        System.out.println("TESTING: " + file.getAbsolutePath());
        ContainerMessage cm = loadDocument(file.getAbsolutePath());
        cm = controller.validate(cm, Endpoint.TEST);

        List<DocumentError> errors = cm.getDocumentInfo().getErrors();
        List<DocumentWarning> warnings = cm.getDocumentInfo().getWarnings();
        if (errors.isEmpty()) {
            System.out.println("PASSED: " + file.getAbsolutePath() + " [" + errors.size() + " error(s), " + warnings.size() + " warning(s)]");
        } else {
            System.out.println("FAILED: " + file.getAbsolutePath() + " [" + errors.size() + " error(s), " + warnings.size() + " warning(s)]");
        }

        System.out.println("------------------------------------------");
        for (DocumentError de : errors) {
            System.out.println("E: " + (de.getValidationError() == null ? "null" : de.getValidationError().getIdentifier()));
        }
        for (DocumentError dw : warnings) {
            System.out.println("W: " + (dw.getValidationError() == null ? "null" : dw.getValidationError().getIdentifier()));
        }
    }

    @Test
    @Ignore("sometimes we get mass amount of files from SD guys to be validated manually")
    public void goThroughMassFolder() throws Exception {
        File dir = new File(ValidationControllerTest.class.getResource("/test-materials/mass").getFile());
        assertNotNull(dir.list());
        String[] list = dir.list();

        for (String fileName : list) {
            File file = new File(dir, fileName);
            ContainerMessage cm = loadDocument(file.getAbsolutePath());
            cm = controller.validate(cm, Endpoint.TEST);

            if (cm.getDocumentInfo().getErrors().size() > 0) {
                System.out.println("FAILED: " + file.getName() + " [" + cm.getDocumentInfo().getErrors().size() + " error(s)]");
                for (DocumentError e : cm.getDocumentInfo().getErrors()) {
                    System.out.println("     error = [" + e.getValidationError().getText() + "], location= [" + e.getValidationError().getLocation() + "]");
                }
            }
        }

    }

    private void processFile(@NotNull File file) throws Exception {
        List<String> expected = getExpected(file);

        System.out.println("TESTING: " + file.getAbsolutePath());
        ContainerMessage cm = loadDocument(file.getAbsolutePath());
        cm = controller.validate(cm, Endpoint.TEST);

        assertTrue(compare(cm, expected));
        System.out.println("PASSED: " + file.getAbsolutePath() +
                " [" + cm.getDocumentInfo().getErrors().size() + " error(s), " +
                cm.getDocumentInfo().getWarnings().size() + " warning(s)]");
    }

    // an assumption is made that file is formatted
    private List<String> getExpected(@NotNull File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
        List<String> expected = getExpected(lines, new ArrayList<>(), "Error", "E: ");
        return getExpected(lines, expected, "Warning", "W: ");
    }

    private List<String> getExpected(@NotNull List<String> lines, @NotNull List<String> expected, @NotNull String header, @NotNull String prefix) {
        boolean reading = false;

        for (String line : lines) {
            line = line.trim();
            if (reading) {
                if (line.equals("") || "none".equalsIgnoreCase(line) || "-->".equals(line)) {
                    return expected;
                }
                if (line.contains(" x ")) {
                    // PEPPOL_CORE_R001 x 6
                    String parts[] = line.split(" x ");
                    for (int i = 0; i < Integer.parseInt(parts[1]); i++) {
                        expected.add(prefix + parts[0]);
                    }
                } else {
                    // BII2-T10-R026
                    expected.add(prefix + line);
                }
            }

            if (line.startsWith(header)) {
                reading = true;
            }
        }

        return expected;
    }

    @SuppressWarnings("ConstantConditions")
    private boolean compare(@NotNull ContainerMessage cm, @NotNull List<String> expected) {
        boolean passed = true;
        for (DocumentError de : cm.getDocumentInfo().getErrors()) {
            assertNotNull("Check that it is validation error, not some other", de.getValidationError());
            String line = "E: " +
                    (de.getValidationError() == null ? "null" : de.getValidationError().getIdentifier());
            if (expected.contains(line)) {
                expected.remove(line);
            } else {
                System.err.println("Unexpected error: " + line + " in file " + cm.getFileName());
                System.err.println("\t" + de.getMessage());
                passed = false;
            }
        }
        for (DocumentWarning dw : cm.getDocumentInfo().getWarnings()) {
            assertNotNull(dw.getValidationError());
            String line = "W: " +
                    (dw.getValidationError() == null ? "null" : dw.getValidationError().getIdentifier());
            if (expected.contains(line)) {
                expected.remove(line);
            } else {
                System.err.println("Unexpected warning: " + line + " in file " + cm.getFileName());
                System.err.println("\t" + dw.getMessage());
                passed = false;
            }
        }
        for (String s : expected) {
            System.err.println("Expected error/warning " + s + " not catch in file " + cm.getFileName());
            passed = false;
        }
        return passed;
    }

    private ContainerMessage loadDocument(String file) throws Exception {
        DocumentInfo info = documentLoader.load(file, Endpoint.TEST);
        System.out.println("Document " + file + " recognized as " + info.getArchetype());
        return new ContainerMessage(file, Endpoint.TEST).setDocumentInfo(info);
    }

}
