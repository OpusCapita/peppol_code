package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.UblDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.junit.Test;

import java.util.Arrays;
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

        RoutingController rc = new RoutingController(conf, null);

        ContainerMessage cm;

        cm = new ContainerMessage(new UblDocument(), "test", Endpoint.PEPPOL);
        assertNull(cm.getRoute());
        cm = rc.loadRoute(cm);
        assertNotNull(cm.getRoute());
        assertEquals("r2", cm.getRoute().getDescription());
        assertEquals("r2a", cm.getRoute().pop());

        cm = new ContainerMessage(new UblDocument(), "test", Endpoint.GATEWAY);
        assertNull(cm.getRoute());
        cm = rc.loadRoute(cm);
        assertNotNull(cm.getRoute());
        assertEquals("r1", cm.getRoute().getDescription());
        assertEquals("r1a", cm.getRoute().pop());
    }

}