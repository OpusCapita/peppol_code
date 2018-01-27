package com.opuscapita.peppol.proxy.filters.pre;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by bambr on 16.28.12.
 */
public class AccessCheckFilterTest {
    private final static String ZUUL_SERVLET_PATH = "/peppol-ap-inbound";

    // filter throws NPEs in production, let's try to get it here
    @Test
    public void someNulls() {
        AccessFilterProperties properties = new AccessFilterProperties("*", null, null, null, null, null, null);
        AccessCheckFilter filter = new AccessCheckFilter(properties, ZUUL_SERVLET_PATH);

        filter.run();
    }

    @Test
    public void isNotAllowed() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getRemoteAddr()).thenReturn("192.168.1.100");
        when(requestMock.getRequestURI()).thenReturn("/validator/");
        HttpServletRequest inboundRequestMock = mock(HttpServletRequest.class);
        when(inboundRequestMock.getRemoteAddr()).thenReturn("10.10.10.10");
        when(inboundRequestMock.getRequestURI()).thenReturn("/as2/status");
        AccessFilterProperties globalAllowedEverything = new AccessFilterProperties("*", null, null, null, null, null, null);
        AccessCheckFilter testSubject = new AccessCheckFilter(globalAllowedEverything, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("1");
        AccessFilterProperties globalDeniedEverything = new AccessFilterProperties(null, "*", null, null, null, null, null);
        testSubject = new AccessCheckFilter(globalDeniedEverything, ZUUL_SERVLET_PATH);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("2");
        AccessFilterProperties subnetAllowed = new AccessFilterProperties("192.168.1.0/8", null, null, null, null, null, null);
        testSubject = new AccessCheckFilter(subnetAllowed, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("3");
        AccessFilterProperties subnetAllowedAllDenied = new AccessFilterProperties("192.168.1.0/8", null, null, null, null, null, null);
        testSubject = new AccessCheckFilter(subnetAllowedAllDenied, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("4");
        AccessFilterProperties subnetAllowedAllDeniedNegative = new AccessFilterProperties("192.168.0.0/12", null, null, null, null, null, null);
        testSubject = new AccessCheckFilter(subnetAllowedAllDeniedNegative, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("5");
        AccessFilterProperties subnetDeniedAllAllowed = new AccessFilterProperties("*", "192.168.1.0/8", null, null, null, null, null);
        testSubject = new AccessCheckFilter(subnetDeniedAllAllowed, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("6");
        AccessFilterProperties serviceAllowed = new AccessFilterProperties(null, null, null, null, null, new HashMap<String, String>() {{
            put("validator", "192.168.1.100/1");
        }}, null);
        testSubject = new AccessCheckFilter(serviceAllowed, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("7");
        AccessFilterProperties serviceAllowedWildcard = new AccessFilterProperties(null, null, null, null, null, new HashMap<String, String>() {{
            put("validator", "*");
        }}, null);
        testSubject = new AccessCheckFilter(serviceAllowedWildcard, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("8");
        AccessFilterProperties serviceDenied = new AccessFilterProperties(null, null, null, null, null, null, new HashMap<String, String>() {{
            put("validator", "192.168.1.0/8");
        }});
        testSubject = new AccessCheckFilter(serviceDenied, ZUUL_SERVLET_PATH);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("9");
        AccessFilterProperties serviceDeniedFromAnyAddress = new AccessFilterProperties(null, null, null, null, null, null, new HashMap<String, String>() {{
            put("validator", "*");
        }});
        testSubject = new AccessCheckFilter(serviceDeniedFromAnyAddress, ZUUL_SERVLET_PATH);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("10");
        AccessFilterProperties specificIpRangePerFilter = new AccessFilterProperties("10.0.0.0/24", "*", null, null, null, new HashMap<String, String>() {{
            put("as2", "*");
        }}, null);
        testSubject = new AccessCheckFilter(specificIpRangePerFilter, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(inboundRequestMock));
        System.out.println("11");
        AccessFilterProperties specificIpRangePerFilterWithProhibitedMask = new AccessFilterProperties("10.0.0.0/24", "*", null, "admin, status", null, new HashMap<String, String>() {{
            put("as2", "*");
        }}, null);
        testSubject = new AccessCheckFilter(specificIpRangePerFilterWithProhibitedMask, ZUUL_SERVLET_PATH);
        assertTrue(testSubject.isNotAllowed(inboundRequestMock));
        System.out.println("12");
        AccessFilterProperties specificIpRangePerFilterWithProhibitedMaskAndNetworkOverrides = new AccessFilterProperties("10.0.0.0/24", "*", null, "admin, status", "10.10.10.0/24, 192.0.0.0/24", new HashMap<String, String>() {{
            put("as2", "*");
        }}, null);
        testSubject = new AccessCheckFilter(specificIpRangePerFilterWithProhibitedMaskAndNetworkOverrides, ZUUL_SERVLET_PATH);
        assertFalse(testSubject.isNotAllowed(inboundRequestMock));
        System.out.println("13");
    }

}