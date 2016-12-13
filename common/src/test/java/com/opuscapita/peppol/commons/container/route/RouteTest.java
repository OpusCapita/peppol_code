package com.opuscapita.peppol.commons.container.route;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static com.opuscapita.peppol.commons.container.route.Route.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Sergejs.Roze
 */
public class RouteTest {

    @Test
    public void testSimplePop() throws Exception {
        Route route = new Route();
        route.setEndpoints(Arrays.asList("A", "B" + PARAMETERS_SEPARATOR + "delay" + VALUE_SEPARATOR + "2000", "C", "D"));

        assertEquals("A", route.pop());
        assertEquals("B", route.pop());
        assertEquals("C", route.pop());
        assertEquals("D", route.pop());
    }

    @Test
    public void testWithParameters() throws Exception {
        Route route = new Route();
        route.setEndpoints(Arrays.asList("A", "B" + PARAMETERS_SEPARATOR + "delay" + VALUE_SEPARATOR + "2000" + PAIRS_SEPARATOR
                + "last"));

        assertEquals("A", route.popFull().get(NEXT));

        Map<String, String> b = route.popFull();
        assertEquals("B", b.get(NEXT));
        assertEquals("2000", b.get("delay"));
        assertTrue(b.containsKey("last"));
    }

}
