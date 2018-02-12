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
        ValidationRule rule = validationRules.getByDocumentType("unit-test");
        assertNotNull(rule);
        assertEquals(2, rule.getRules().size());
        assertEquals(1, rule.getSuppress().size());
    }

}
