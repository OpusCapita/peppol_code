package com.opuscapita.peppol.proxy.filters.pre.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by bambr on 17.20.1.
 */
public class RequestUtils {
    private final static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    public static String extractRequestedService(HttpServletRequest request, String zuulServletPath) {
        logger.debug("Extracting service from: " + request.getRequestURI());
        logger.debug("Context path: " + request.getContextPath());
        logger.debug("Query string: " + request.getQueryString());
        String requestUri = request.getRequestURI();
        if (zuulServletPath != null) {
            requestUri = requestUri.replaceFirst(zuulServletPath, "");
        }
        String[] requestParts = requestUri.split("/");
        if (requestParts.length > 1) {
            Optional<String> first = Arrays.asList(requestParts).stream().filter(part -> !part.isEmpty()).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return request.getRequestURI().replaceAll("/", "");
    }
}
