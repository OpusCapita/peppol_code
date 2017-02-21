package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.validator.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * Created by bambr on 17.20.2.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class SenderReceiverConsistencyValidatorTest extends TestCommon {
    @Autowired
    DocumentLoader documentLoader;

    SenderReceiverConsistencyValidator validator;
    List<String> expectedToFail = new ArrayList<String>() {
        {
            add("invalids1-list-ejt.xml");
        }
    };
    private String[] documentProfilesToBeTested = {/*"svefaktura1",*/ /*"austria",*/ "difi"/*, "simpler_invoicing"*/};

    @Before
    public void setUp() throws Exception {
        validator = new SenderReceiverConsistencyValidator();
    }

    @After
    public void tearDown() throws Exception {
        validator = null;
    }

    @Test
    public void senderAndReceiverAreSame() throws Exception {
        Arrays.asList(documentProfilesToBeTested).stream().forEach(profile -> runTestsOnDocumentProfile(profile, (File file) -> {
            try {
                ContainerMessage containerMessage = createContainerMessageFromFile(documentLoader, file);
                if (containerMessage == null) {
                    return;
                }
                boolean achievedResult = validator.senderAndReceiverAreSame(containerMessage).isPassed();
                if (!achievedResult && !expectedToFail.contains(file.getName())) {
                    fail("Failed consistency check");
                }
            } catch (IOException e) {
                e.printStackTrace();
                fail("Failed with exception: " + e.getMessage());
            }
        }));
    }

}