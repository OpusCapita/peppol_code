package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.TestConfig;
import com.opuscapita.peppol.validator.validations.common.TestCommon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.Assert.fail;

/**
 * Created by bambr on 16.7.10.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ValidationControllerTest extends TestCommon {
    private String[] documentProfilesToBeTested = {"svefaktura1", "austria", "difi", "simpler_invoicing"};

    @Autowired
    private ValidationController validationController;

    @Autowired
    private DocumentLoader documentLoader;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }


    @Test
    //@Ignore("Caused by: org.springframework.amqp.rabbit.listener.QueuesNotAvailableException")
    public void validateSveFaktura1Files() throws Exception {
        Arrays.stream(documentProfilesToBeTested).forEach(this::testDocumentProfileValidation);
    }

    @SuppressWarnings("ConstantConditions")
    private void testDocumentProfileValidation(final String documentProfile) {
        Consumer<? super File> consumer = (File file) -> {
            try {
                ContainerMessage containerMessage = createContainerMessageFromFile(documentLoader, file);
                if (containerMessage == null) return;

                ValidationResult result = validationController.validate(containerMessage);
                System.out.println("result: " + result.isPassed() + " on " + file.getName());
                result.getErrors().forEach(System.out::println);
                if ((result.isPassed() && file.getName().contains("invalid"))
                        || (!result.isPassed() && file.getName().contains("Valid")
                        && !file.getName().contains("invalid"))) {
                    fail("Failed on expected validation result: " + result.isPassed() + " on " + file.getName()+ " [" + documentProfile + "]");
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed with exception: " + e.getMessage());
            }
        };
        runTestsOnDocumentProfile(documentProfile, consumer);
    }

}
