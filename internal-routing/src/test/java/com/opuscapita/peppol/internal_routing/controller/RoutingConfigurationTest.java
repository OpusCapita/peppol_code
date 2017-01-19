package com.opuscapita.peppol.internal_routing.controller;

import org.junit.Ignore;
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
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
public class RoutingConfigurationTest {
    @Autowired
    private RoutingConfiguration routingConfiguration;

    @Test
    @Ignore
    public void testLoader() {
        assertNotNull(routingConfiguration);
        assertEquals(4, routingConfiguration.getRoutes().size());
        assertEquals(2, routingConfiguration.getRoutes().get(0).getEndpoints().size());
    }
}
