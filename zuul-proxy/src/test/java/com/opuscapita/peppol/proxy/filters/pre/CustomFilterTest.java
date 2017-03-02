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
public class CustomFilterTest {
    @Test
    public void isNotAllowed() throws Exception {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getRemoteAddr()).thenReturn("192.168.1.100");
        when(requestMock.getRequestURI()).thenReturn("/validator/");
        HttpServletRequest inboundRequestMock = mock(HttpServletRequest.class);
        when(inboundRequestMock.getRemoteAddr()).thenReturn("10.10.10.10");
        when(inboundRequestMock.getRequestURI()).thenReturn("/peppol-ap-inbound/status");
        FilterProperties globalAllowedEverything = new FilterProperties("*", null, null, null);
        CustomFilter testSubject = new CustomFilter(globalAllowedEverything);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("1");
        FilterProperties globalDeniedEverything = new FilterProperties(null, "*", null, null);
        testSubject = new CustomFilter(globalDeniedEverything);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("2");
        FilterProperties subnetAllowed = new FilterProperties("192.168.1.0/8", null, null, null);
        testSubject = new CustomFilter(subnetAllowed);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("3");
        FilterProperties subnetAllowedAllDenied = new FilterProperties("192.168.1.0/8", null, null, null);
        testSubject = new CustomFilter(subnetAllowedAllDenied);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("4");
        FilterProperties subnetAllowedAllDeniedNegative = new FilterProperties("192.168.0.0/12", null, null, null);
        testSubject = new CustomFilter(subnetAllowedAllDeniedNegative);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("5");
        FilterProperties subnetDeniedAllAllowed = new FilterProperties("*", "192.168.1.0/8", null, null);
        testSubject = new CustomFilter(subnetDeniedAllAllowed);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("6");
        FilterProperties serviceAllowed = new FilterProperties(null, null, new HashMap<String, String>() {{
            put("validator", "192.168.1.100/1");
        }}, null);
        testSubject = new CustomFilter(serviceAllowed);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("7");
        FilterProperties serviceAllowedWildcard = new FilterProperties(null, null, new HashMap<String, String>() {{
            put("validator", "*");
        }}, null);
        testSubject = new CustomFilter(serviceAllowedWildcard);
        assertFalse(testSubject.isNotAllowed(requestMock));
        System.out.println("8");
        FilterProperties serviceDenied = new FilterProperties(null, null, null, new HashMap<String, String>(){{
            put("validator", "192.168.1.0/8");
        }});
        testSubject = new CustomFilter(serviceDenied);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("9");
        FilterProperties serviceDeniedFromAnyAddress = new FilterProperties(null, null, null, new HashMap<String, String>(){{
            put("validator", "*");
        }});
        testSubject = new CustomFilter(serviceDeniedFromAnyAddress);
        assertTrue(testSubject.isNotAllowed(requestMock));
        System.out.println("10");
        FilterProperties specificIpRangePerFilter = new FilterProperties("10.0.0.0/24", "*", new HashMap<String, String>() {{
            put("peppol-ap-inbound", "*");
        }}, null);
        testSubject = new CustomFilter(specificIpRangePerFilter);
        assertFalse(testSubject.isNotAllowed(inboundRequestMock));
        System.out.println("11");
    }

}