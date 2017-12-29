package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.validator.controller.ValidationControllerTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergejs.Roze
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ValidationControllerTestConfig.class)
@TestPropertySource(locations="classpath:application.yml")
public class RuleSetConfigTest {
    @Autowired
    private RuleSetConfig ruleSetConfig;

    @Test
    public void testLoading() throws Exception {
        assertNotNull(ruleSetConfig);
        assertNotNull(ruleSetConfig.getTypes());
        List<String> found = ruleSetConfig.getRules("urn:www|cenbii|eu:profile:bii01:ver2|0###urn:www|cenbii|eu:transaction:biitrns019:ver2|0:extended:urn:www|peppol|eu:bis:peppol1a:ver2|0");
        assertNotNull(found);
        assertEquals(4, found.size());
        assertEquals("eu.peppol.postaward.ver20-e5085a49db8fcf702be9109bf932295f4e838275/xsl/BIIRULES-UBL-T19.xsl", found.get(0));
    }

}
