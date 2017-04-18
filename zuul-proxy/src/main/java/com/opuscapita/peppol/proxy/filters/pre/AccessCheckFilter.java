package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opuscapita.peppol.proxy.filters.pre.util.RequestUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * Created by bambr on 16.28.12.
 */
public class AccessCheckFilter extends ZuulFilter {
    private final static Logger logger = LoggerFactory.getLogger(AccessCheckFilter.class);
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private final AccessFilterProperties accessFilterProperties;

    public AccessCheckFilter(AccessFilterProperties accessFilterProperties) {
        this.accessFilterProperties = accessFilterProperties;
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
        String requestedService = RequestUtils.extractRequestedService(request);
        boolean result = !accessFilterProperties.getServicesToBypass().contains(requestedService);
        logger.debug("Should filter for service ["+requestedService+"]: "+result);
        return result;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        logger.debug(request.getRemoteAddr());
        if (isNotAllowed(request)) {
            try {
                requestContext.getResponse().sendError(500, "Denied!!!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected boolean isNotAllowed(HttpServletRequest request) {
        String requestedService = RequestUtils.extractRequestedService(request);
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

        final String finalRemoteAddr = remoteAddr;

        logger.debug("Checking against address: " + remoteAddr + " and service: " + requestedService);
        if (requestedService.isEmpty()) {
            requestedService = "/";
        }
        boolean result = true;

        //Deny global settings
        if (accessFilterProperties.getDenyFrom() != null && accessFilterProperties.getDenyFrom().contains("*")) {
            result = true;
        } else {
            if (accessFilterProperties.getDenyFrom() != null) {
                result = accessFilterProperties.getDenyFrom().stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
            }
        }

        //Allow global settings
        if (accessFilterProperties.getAllowFrom() != null && accessFilterProperties.getAllowFrom().contains("*")) {
            result = false;
        } else {
            if (accessFilterProperties.getAllowFrom() != null) {
                result = !accessFilterProperties.getAllowFrom().stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
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

        //Allow  service level settings
        if (accessFilterProperties.getServicesAllowFrom() != null) {
            if (accessFilterProperties.getServicesAllowFrom().containsKey(requestedService)) {
                if (accessFilterProperties.getServicesAllowFrom().get(requestedService).contains("*")) {
                    result = false;
                } else {
                    result = !accessFilterProperties.getServicesAllowFrom().get(requestedService).stream().map(entry -> new SubnetUtils(entry)).anyMatch(subnet -> subnet.getInfo().isInRange(finalRemoteAddr));
                }
            }
        }

        return result;
    }

}
