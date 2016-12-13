package com.opuscapita.peppol.commons.container.route;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergejs.Roze
 */
public class RouteTest {

    @Test
    public void testSimplePop() throws Exception {
        Route route = new Route();
        route.setEndpoints(Arrays.asList("A", "B" + Route.PARAMETERS_SEPARATOR + "delay=2000", "C", "D"));

        assertEquals("A", route.pop());
        assertEquals("B", route.pop());
        assertEquals("C", route.pop());
        assertEquals("D", route.pop());
    }

    @Test
    public void testWithParameters() throws Exception {
        Route route = new Route();
        route.setEndpoints(Arrays.asList("A", "B" + Route.PARAMETERS_SEPARATOR + "delay=2000"));

        assertEquals("A", route.popWithParameters());
        assertEquals("B" + Route.PARAMETERS_SEPARATOR + "delay=2000", route.popWithParameters());

    }

}
