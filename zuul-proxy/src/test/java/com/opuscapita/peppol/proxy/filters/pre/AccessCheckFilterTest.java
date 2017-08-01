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
    private String zuulServletPath = "/peppol-ap-inbound";

    @Test
    public void isNotAllowed() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getRemoteAddr()).thenReturn("192.168.1.100");
        when(requestMock.getRequestURI()).thenReturn("/validator/");
        HttpServletRequest inboundRequestMock = mock(HttpServletRequest.class);
        when(inboundRequestMock.getRemoteAddr()).thenReturn("10.10.10.10");
        when(inboundRequestMock.getRequestURI()).thenReturn("/as2/status");
        AccessFilterProperties globalAllowedEverything = new AccessFilterProperties("*", null,null, null, null);
        AccessCheckFilter testSubject = new AccessCheckFilter(globalAllowedEverything, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("1");
        AccessFilterProperties globalDeniedEverything = new AccessFilterProperties(null, "*",null, null, null);
        testSubject = new AccessCheckFilter(globalDeniedEverything, zuulServletPath);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("2");
        AccessFilterProperties subnetAllowed = new AccessFilterProperties("192.168.1.0/8", null,null, null, null);
        testSubject = new AccessCheckFilter(subnetAllowed, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("3");
        AccessFilterProperties subnetAllowedAllDenied = new AccessFilterProperties("192.168.1.0/8", null, null, null, null);
        testSubject = new AccessCheckFilter(subnetAllowedAllDenied, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("4");
        AccessFilterProperties subnetAllowedAllDeniedNegative = new AccessFilterProperties("192.168.0.0/12", null,null,  null, null);
        testSubject = new AccessCheckFilter(subnetAllowedAllDeniedNegative, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("5");
        AccessFilterProperties subnetDeniedAllAllowed = new AccessFilterProperties("*", "192.168.1.0/8", null, null, null);
        testSubject = new AccessCheckFilter(subnetDeniedAllAllowed, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("6");
        AccessFilterProperties serviceAllowed = new AccessFilterProperties(null, null, null, new HashMap<String, String>() {{
            put("validator", "192.168.1.100/1");
        }}, null);
        testSubject = new AccessCheckFilter(serviceAllowed, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("7");
        AccessFilterProperties serviceAllowedWildcard = new AccessFilterProperties(null, null, null, new HashMap<String, String>() {{
            put("validator", "*");
        }}, null);
        testSubject = new AccessCheckFilter(serviceAllowedWildcard, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("8");
        AccessFilterProperties serviceDenied = new AccessFilterProperties(null, null, null,null,  new HashMap<String, String>(){{
            put("validator", "192.168.1.0/8");
        }});
        testSubject = new AccessCheckFilter(serviceDenied, zuulServletPath);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("9");
        AccessFilterProperties serviceDeniedFromAnyAddress = new AccessFilterProperties(null, null, null, null, new HashMap<String, String>(){{
            put("validator", "*");
        }});
        testSubject = new AccessCheckFilter(serviceDeniedFromAnyAddress, zuulServletPath);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("10");
        AccessFilterProperties specificIpRangePerFilter = new AccessFilterProperties("10.0.0.0/24", "*",null, new HashMap<String, String>() {{
            put("as2", "*");
        }}, null);
        testSubject = new AccessCheckFilter(specificIpRangePerFilter, zuulServletPath);
        assertFalse(testSubject.isNotAllowed(inboundRequestMock));
        System.out.println("11");
    }

}