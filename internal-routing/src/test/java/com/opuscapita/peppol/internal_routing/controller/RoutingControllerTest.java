package com.opuscapita.peppol.internal_routing.controller;

import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
public class RoutingControllerTest {
    private ErrorHandler errorHandler = mock(ErrorHandler.class);
    private StatusReporter statusReporter = mock(StatusReporter.class);

    @Test
    public void loadRoute() throws Exception { // TODO
//        Route r1 = new Route();
//        r1.setDescription("r1");
//        r1.setEndpoints(Arrays.asList("r1a", "r1b", "r1c"));
//        r1.setSource("test1");
//
//        Route r2 = new Route();
//        r2.setDescription("r2");
//        r2.setEndpoints(Arrays.asList("r2a", "r2b"));
//        r2.setSource("test2");
//
//        RoutingConfiguration conf = new RoutingConfiguration() {
//            @Override
//            public List<Route> getRoutes() {
//                return Arrays.asList(r1, r2);
//            }
//        };
//
//        RoutingController rc = new RoutingController(conf, errorHandler, statusReporter);
//
//        ContainerMessage cm;
//
//        cm = new ContainerMessage("test", "test", new Endpoint("test2", ProcessType.TEST)).setDocumentInfo(new UblDocument());
//        assertNull(cm.getRoute());
//        cm = rc.loadRoute(cm);
//        assertNotNull(cm.getRoute());
//        assertEquals("r2", cm.getRoute().getDescription());
//        assertEquals("r2a", cm.getRoute().pop());
//        assertEquals("r2b", cm.getRoute().pop());
//
//        cm = new ContainerMessage("test", "test", new Endpoint("test1", ProcessType.TEST)).setDocumentInfo(new UblDocument());
//        assertNull(cm.getRoute());
//        cm = rc.loadRoute(cm);
//        assertNotNull(cm.getRoute());
//        assertEquals("r1", cm.getRoute().getDescription());
//        assertEquals("r1a", cm.getRoute().pop());
//        assertEquals("r1b", cm.getRoute().pop());
//        assertEquals("r1c", cm.getRoute().pop());
    }

    // there was a bug that route defined once was assigned to different objects and damaged
    @SuppressWarnings("ConstantConditions")
    @Test
    public void loadTwice() throws Exception { // TODO
//        Route route = new Route();
//        route.setSource("test");
//        route.setEndpoints(Arrays.asList("A", "B", "C"));
//
//        RoutingConfiguration conf = new RoutingConfiguration() {
//            @Override
//            public List<Route> getRoutes() {
//                return Collections.singletonList(route);
//            }
//        };
//
//        RoutingController rc = new RoutingController(conf, errorHandler, statusReporter);
//
//        ContainerMessage cm1 = new ContainerMessage("test1", "test1", new Endpoint("test", ProcessType.TEST));
//        ContainerMessage cm2 = new ContainerMessage("test2", "test2", new Endpoint("test", ProcessType.TEST));
//
//        cm1 = rc.loadRoute(cm1);
//        assertEquals("A", cm1.getRoute().pop());
//        assertEquals("B", cm1.getRoute().pop());
//
//        cm2 = rc.loadRoute(cm2);
//        assertEquals("A", cm2.getRoute().pop());
//        assertEquals("C", cm1.getRoute().pop());
//        assertEquals("B", cm2.getRoute().pop());
    }

}
