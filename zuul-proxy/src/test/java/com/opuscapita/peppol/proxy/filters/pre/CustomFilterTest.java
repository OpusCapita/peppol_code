package com.opuscapita.peppol.proxy.filters.pre;

import com.opuscapita.peppol.proxy.filters.pre.CustomFilter;
import com.opuscapita.peppol.proxy.filters.pre.FilterProperties;
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
        FilterProperties globalDeniedEverything = new FilterProperties(null, "*", null, null);
        testSubject = new CustomFilter(globalDeniedEverything);
        assertTrue(testSubject.isNotAllowed(requestMock));
        FilterProperties subnetAllowed = new FilterProperties("192.168.1", null, null, null);
        testSubject = new CustomFilter(subnetAllowed);
        assertFalse(testSubject.isNotAllowed(requestMock));
        FilterProperties subnetAllowedAllDenied = new FilterProperties("192.168.1", null, null, null);
        testSubject = new CustomFilter(subnetAllowedAllDenied);
        assertFalse(testSubject.isNotAllowed(requestMock));
        FilterProperties subnetAllowedAllDeniedNegative = new FilterProperties("127.0.0", null, null, null);
        testSubject = new CustomFilter(subnetAllowedAllDeniedNegative);
        assertTrue(testSubject.isNotAllowed(requestMock));
        FilterProperties subnetDeniedAllAllowed = new FilterProperties("*", "192.168.1", null, null);
        testSubject = new CustomFilter(subnetDeniedAllAllowed);
        assertTrue(testSubject.isNotAllowed(requestMock));
        FilterProperties serviceAllowed = new FilterProperties(null, null, new HashMap<String, String>() {{
            put("validator", "192.168.1.100");
        }}, null);
        testSubject = new CustomFilter(serviceAllowed);
        assertFalse(testSubject.isNotAllowed(requestMock));
        FilterProperties serviceAllowedWildcard = new FilterProperties(null, null, new HashMap<String, String>() {{
            put("validator", "*");
        }}, null);
        testSubject = new CustomFilter(serviceAllowedWildcard);
        assertFalse(testSubject.isNotAllowed(requestMock));
        FilterProperties serviceDenied = new FilterProperties(null, null, null, new HashMap<String, String>(){{
            put("validator", "192.168.1");
        }});
        testSubject = new CustomFilter(serviceDenied);
        assertTrue(testSubject.isNotAllowed(requestMock));
        FilterProperties serviceDeniedFromAnyAddress = new FilterProperties(null, null, null, new HashMap<String, String>(){{
            put("validator", "*");
        }});
        testSubject = new CustomFilter(serviceDeniedFromAnyAddress);
        assertTrue(testSubject.isNotAllowed(requestMock));

        FilterProperties specificIpRangePerFilter = new FilterProperties("10.", "*", new HashMap<String, String>(){{
            put("peppol-ap-inbound", "*");
        }}, null);
        testSubject = new CustomFilter(specificIpRangePerFilter);
        assertFalse(testSubject.isNotAllowed(inboundRequestMock));

    }

}