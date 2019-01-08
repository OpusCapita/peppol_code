package com.opuscapita.peppol.proxy.filters.pre;

import com.google.common.collect.Iterables;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opuscapita.peppol.proxy.filters.pre.util.RequestUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by bambr on 16.28.12.
 */
public class AccessCheckFilter extends ZuulFilter {

    private final static Logger logger = LoggerFactory.getLogger(AccessCheckFilter.class);
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private final AccessFilterProperties accessFilterProperties;
    private final String zuulServletPath;

    public AccessCheckFilter(AccessFilterProperties accessFilterProperties, String zuulServletPath) {
        this.accessFilterProperties = accessFilterProperties;
        this.zuulServletPath = zuulServletPath;

        logger.info("ZuulServletPath: " + zuulServletPath);
        logger.info(accessFilterProperties.toString());
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestedService = RequestUtils.extractRequestedService(request, zuulServletPath);
        boolean result = !accessFilterProperties.getServicesToBypass().contains(requestedService);
        logger.info("Should filter for service [" + requestedService + "]: " + result);
        return result;
    }

    @Override
    public Object run() {
        try {
            RequestContext requestContext = RequestContext.getCurrentContext();
            HttpServletRequest request = requestContext.getRequest();
            if (isNotAllowed(request)) {
                reject(requestContext);
            }
            return null;
        } catch (Exception e) {
            logger.error("Filter threw an exception: " + e.getMessage(), e);
            reject(RequestContext.getCurrentContext());
            return null;
        }
    }

    private void reject(RequestContext context) {
        try {
            context.unset();
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        } catch (Exception e) {
            logger.error("Failed to set response status: " + e.getMessage(), e);
        }
    }

    boolean isNotAllowed(HttpServletRequest request) {
        String requestedService = RequestUtils.extractRequestedService(request, zuulServletPath);
        String finalRemoteAddr = extractRemoteAddress(request);
        if (requestedService.isEmpty()) {
            requestedService = "/";
        }

        boolean result = true;
        logger.info("Running proxy for service: " + requestedService + " requested from: " + finalRemoteAddr);

        if (containsProhibitedMasks(request, accessFilterProperties.getProhibitedMasks())
                &&
                !hasProhibitedMasksNetworkOverride(finalRemoteAddr, accessFilterProperties.getProhibitedMasksNetworkOverrides())) {
            return true;
        }

        //Deny global settings
        if (accessFilterProperties.getDenyFrom() != null && accessFilterProperties.getDenyFrom().contains("*")) {
            result = true;
            logger.info("Not allowing request since accessFilterProperties.denyFrom set to * ");
        } else {
            if (accessFilterProperties.getDenyFrom() != null) {
                result = accessFilterProperties.getDenyFrom().stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
                if (result) {
                    logger.info("Not allowing request since accessFilterProperties.denyFrom includes " + finalRemoteAddr);
                }
            }
        }

        //Allow global settings
        if (accessFilterProperties.getAllowFrom() != null && accessFilterProperties.getAllowFrom().contains("*")) {
            result = false;
            logger.info("Allowing request since accessFilterProperties.allowFrom set to * ");
        } else {
            if (accessFilterProperties.getAllowFrom() != null) {
                result = !accessFilterProperties.getAllowFrom().stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
                if (!result) {
                    logger.info("Allowing request since accessFilterProperties.allowFrom includes " + finalRemoteAddr);
                }
            }
        }

        //Deny service level settings
        if (accessFilterProperties.getServicesDenyFrom() != null) {
            if (accessFilterProperties.getServicesDenyFrom().containsKey(requestedService)) {
                if (accessFilterProperties.getServicesDenyFrom().get(requestedService).contains("*")) {
                    result = true;
                } else {
                    result = accessFilterProperties.getServicesDenyFrom().get(requestedService).stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
                }
            }
        }

        //Allow service level settings
        if (accessFilterProperties.getServicesAllowFrom() != null) {
            if (accessFilterProperties.getServicesAllowFrom().containsKey(requestedService)) {
                // TODO fix this hack
                if (!(accessFilterProperties.getServicesAllowFrom().get(requestedService) instanceof List)) {
                    logger.warn("Attempting to use string as a list, hello to Daniil... allowing access for now. The value is: " +
                            accessFilterProperties.getServicesAllowFrom().get(requestedService));
                    return false;
                }
                if (accessFilterProperties.getServicesAllowFrom().get(requestedService).contains("*")) {
                    result = false;
                } else {
                    result = !accessFilterProperties.getServicesAllowFrom().get(requestedService).stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
                }
            }
        }

        return result;
    }

    private String extractRemoteAddress(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        logger.debug("remoteAddr: " + remoteAddr);

        String forwardedFor = "";
        // when working behind a proxy we get the original ip in X-Forwarded-For
        // while remoteAddr is the ip of the proxy itself
        if (request.getHeaders(HEADER_X_FORWARDED_FOR) != null && request.getHeaders(HEADER_X_FORWARDED_FOR).hasMoreElements()) {
            forwardedFor = request.getHeaders(HEADER_X_FORWARDED_FOR).nextElement();
        }
        logger.debug("forwardedFor: " + forwardedFor);

        if (!forwardedFor.isEmpty()) {
            remoteAddr = forwardedFor;
        }
        return remoteAddr;
    }

    private boolean hasProhibitedMasksNetworkOverride(String finalRemoteAddr, List<String> prohibitedMasksNetworkOverrides) {
        List<String> matchedProhibitedMasksNetworkOverrides = prohibitedMasksNetworkOverrides.stream().filter(override -> new SubnetUtils(override).getInfo().isInRange(finalRemoteAddr)).collect(Collectors.toList());
        logger.info("Matched subnet(s): " + Iterables.toString(matchedProhibitedMasksNetworkOverrides) + " for " + finalRemoteAddr);
        return matchedProhibitedMasksNetworkOverrides.size() > 0;
    }

    private boolean containsProhibitedMasks(HttpServletRequest request, List<String> prohibitedMasks) {
        String requestUri = request.getRequestURI().toLowerCase();
        List<String> matchedProhibitedMasks = prohibitedMasks.stream().filter(prohibitedMask -> requestUri.contains(prohibitedMask.toLowerCase())).collect(Collectors.toList());
        logger.info("Matched prohibited part(s): " + Iterables.toString(matchedProhibitedMasks) + " in " + requestUri);
        return matchedProhibitedMasks.size() > 0;
    }

}
