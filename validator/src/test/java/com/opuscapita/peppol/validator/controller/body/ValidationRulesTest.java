package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.validator.controller.ValidationControllerTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ValidationControllerTestConfig.class)
@TestPropertySource(locations="classpath:application.yml")
public class ValidationRulesTest {
    @Autowired
    private ValidationRules validationRules;

    @Test
    public void testLoading() {
        assertNotNull(validationRules);
        assertNotNull(validationRules.getMap());
        ValidationRule rule = validationRules.getByDocumentType(
                "urn:www.cenbii.eu:profile:biixy:ver2.0###" +
                        "urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.cenbii.eu:profile:biixy:ver2.0:" +
                        "extended:urn:www.difi.no:ehf:kreditnota:ver2.0");
        assertNotNull(rule);
        assertEquals(6, rule.getRules().size());
        assertNotNull(rule.getSuppress());
        assertEquals(11, rule.getSuppress().size());
    }

}
