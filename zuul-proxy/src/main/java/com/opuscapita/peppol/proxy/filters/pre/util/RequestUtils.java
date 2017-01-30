package com.opuscapita.peppol.proxy.filters.pre.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by bambr on 17.20.1.
 */
public class RequestUtils {
    public static String extractRequestedService(HttpServletRequest request) {
        System.out.println("Extracting service from: " + request.getRequestURI());
        System.out.println("Context path: " + request.getContextPath());
        System.out.println("Query string: " + request.getQueryString());
        String[] requestParts = request.getRequestURI().split("/");
        if(requestParts.length > 1) {
            Optional<String> first = Arrays.asList(requestParts).stream().filter(part -> !part.isEmpty()).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return request.getRequestURI().replaceAll("/", "");
    }
}
