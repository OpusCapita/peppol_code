package com.opuscapita.peppol.proxy.filters.pre.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RequestUtilsTest {

    @Test
    public void testRequestUriExtraction() {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/peppol-ap-inbound/admin/env");
        String zuulServletPath = "/peppol-ap-inbound";
        String service = RequestUtils.extractRequestedService(request, zuulServletPath);
        assertNotNull(service);
        assertEquals("admin", service);
    }

}