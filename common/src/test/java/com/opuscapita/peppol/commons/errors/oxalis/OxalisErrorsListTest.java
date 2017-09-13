package com.opuscapita.peppol.commons.errors.oxalis;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    }

}
