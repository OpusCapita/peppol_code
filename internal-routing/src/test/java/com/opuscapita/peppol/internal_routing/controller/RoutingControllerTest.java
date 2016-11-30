package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.UblDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Sergejs.Roze
 */
public class RoutingControllerTest {

    @Test
    public void loadRoute() throws Exception {
        Route r1 = new Route();
        r1.setDescription("r1");
        r1.setEndpoints(Arrays.asList("r1a", "r1b", "r1c"));
        r1.setSource(Endpoint.GATEWAY);

        Route r2 = new Route();
        r2.setDescription("r2");
        r2.setEndpoints(Arrays.asList("r2a", "r2b"));
        r2.setSource(Endpoint.PEPPOL);

        RoutingConfiguration conf = new RoutingConfiguration() {
            @Override
            public List<Route> getRoutes() {
                return Arrays.asList(r1, r2);
            }
        };

        RoutingController rc = new RoutingController(conf);

        ContainerMessage cm;

        cm = new ContainerMessage("test", "test", Endpoint.PEPPOL).setBaseDocument(new UblDocument());
        assertNull(cm.getRoute());
        cm = rc.loadRoute(cm);
        assertNotNull(cm.getRoute());
        assertEquals("r2", cm.getRoute().getDescription());
        assertEquals("r2a", cm.getRoute().pop());

        cm = new ContainerMessage("test", "test", Endpoint.GATEWAY).setBaseDocument(new UblDocument());
        assertNull(cm.getRoute());
        cm = rc.loadRoute(cm);
        assertNotNull(cm.getRoute());
        assertEquals("r1", cm.getRoute().getDescription());
        assertEquals("r1a", cm.getRoute().pop());
    }

    // there was a bug that route defined once was assigned to different objects and damaged
    @SuppressWarnings("ConstantConditions")
    @Test
    public void loadTwice() throws Exception {
        Route route = new Route();
        route.setSource(Endpoint.PEPPOL);
        route.setEndpoints(Arrays.asList("A", "B", "C"));

        RoutingConfiguration conf = new RoutingConfiguration() {
            @Override
            public List<Route> getRoutes() {
                return Collections.singletonList(route);
            }
        };

        RoutingController rc = new RoutingController(conf);

        ContainerMessage cm1 = new ContainerMessage("test1", "test1", Endpoint.PEPPOL);
        ContainerMessage cm2 = new ContainerMessage("test2", "test2", Endpoint.PEPPOL);

        cm1 = rc.loadRoute(cm1);
        assertEquals("A", cm1.getRoute().pop());
        assertEquals("B", cm1.getRoute().pop());

        cm2 = rc.loadRoute(cm2);
        assertEquals("A", cm2.getRoute().pop());
        assertEquals("C", cm1.getRoute().pop());
        assertEquals("B", cm2.getRoute().pop());
    }

}
