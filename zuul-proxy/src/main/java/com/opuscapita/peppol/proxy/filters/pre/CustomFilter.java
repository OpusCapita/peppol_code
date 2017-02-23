package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opuscapita.peppol.proxy.filters.pre.util.RequestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


/**
 * Created by bambr on 16.28.12.
 */
public class CustomFilter extends ZuulFilter {
    private final static Logger logger = LoggerFactory.getLogger(CustomFilter.class);
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private final FilterProperties filterProperties;

    public CustomFilter(FilterProperties filterProperties) {
        this.filterProperties = filterProperties;
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
        return false;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        System.out.println(request.getRemoteAddr());
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

        System.out.println("Checking against address: "+remoteAddr+" and service: "+ requestedService);
        if (requestedService.isEmpty()) {
            requestedService = "/";
        }
        boolean result = true;
        if("*".equals(filterProperties.getAllowFrom())) {
            result = false;
        }
        if("*".equals(filterProperties.getDenyFrom())) {
            result = true;
        }

        if(filterProperties.getAllowFrom() != null && !filterProperties.getAllowFrom().equals("*")) {
            result = !remoteAddr.startsWith(filterProperties.getAllowFrom());
        }
        if(filterProperties.getServicesAllowFrom() != null) {
            if(filterProperties.getServicesAllowFrom().containsKey(requestedService)) {
                result = !filterProperties.getServicesAllowFrom().get(requestedService).equals("*") && !remoteAddr.startsWith(filterProperties.getServicesAllowFrom().get(requestedService));
            }
        }
        if(filterProperties.getDenyFrom() != null && !filterProperties.getDenyFrom().equals("*")) {
            result = remoteAddr.startsWith(filterProperties.getDenyFrom());
        }
        if(filterProperties.getServicesDenyFrom() != null) {
            if(filterProperties.getServicesDenyFrom().containsKey(requestedService)) {
                result = remoteAddr.startsWith(filterProperties.getServicesDenyFrom().get(requestedService)) || "*".equals(filterProperties.getServicesDenyFrom().get(requestedService));
            }
        }

        return result;
    }

}
