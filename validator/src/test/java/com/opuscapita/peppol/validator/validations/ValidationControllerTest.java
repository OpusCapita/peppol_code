package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.validator.TestConfig;
import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import static org.junit.Assert.fail;

/**
 * Created by bambr on 16.7.10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ValidationControllerTest {
    String[] documentProfilesToBeTested = {"svefaktura1", "austria", "difi", "simpler_invoicing"};

    @Autowired
    ValidationController validationController;

    @Autowired
    DocumentLoader documentLoader;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    public void validateSveFaktura1Files() throws Exception {
        Arrays.stream(documentProfilesToBeTested).forEach(this::testDocumentProfileValidation);
    }

    private void testDocumentProfileValidation(final String documentProfile) {
        File resourceDir = new File(this.getClass().getResource("/test_data/" + documentProfile + "_files").getFile());
        String[] dataFiles = resourceDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("xml");
            }
        });

        Arrays.stream(dataFiles).map(fileName -> {
            System.out.println(fileName);
            File result = new File(resourceDir, fileName);
            return result;
        }).filter(fileToCheck -> fileToCheck.isFile() && fileToCheck.exists()).forEach(file -> {
            try {
                ContainerMessage containerMessage = new ContainerMessage(documentLoader.load(file), file.getName(), Endpoint.PEPPOL);
                ValidationResult result = validationController.validate(containerMessage);
                System.out.println("result: " + result.isPassed() + " on " + file.getName());
                result.getErrors().forEach(error -> System.out.println(error));
                if ((result.isPassed() && file.getName().contains("invalid")) || (!result.isPassed() && file.getName().contains("Valid") && !file.getName().contains("invalid"))) {
                    fail("Failed on expected validation result: " + result.isPassed() + " on " + file.getName());
                }
            } catch (Exception e) {
                fail("Failed with exception: " + e.getMessage());
            }
        });
    }

}