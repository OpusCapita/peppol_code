package com.opuscapita.peppol.commons.errors.oxalis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.opuscapita.peppol.commons.errors.oxalis.SendingErrors.RECEIVING_AP_ERROR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
@Ignore("doesn't work on Estonian computers")
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableConfigurationProperties(value = { OxalisErrorsList.class })
//@TestPropertySource(locations = "classpath:application.yml")
public class OxalisErrorsListTest {
    @Autowired
    private OxalisErrorsList oxalisErrorsList;

    @Test
    public void testConfigurationProperties() throws Exception {
        assertNotNull(oxalisErrorsList);
        assertNotNull(oxalisErrorsList.getList());

        OxalisErrorRecognizer recognizer = new OxalisErrorRecognizer(oxalisErrorsList);
        assertEquals(RECEIVING_AP_ERROR, recognizer.recognize("Failed to recognize error message: Request failed with rc=500, contents received (7199 characters)"));
    }

}
