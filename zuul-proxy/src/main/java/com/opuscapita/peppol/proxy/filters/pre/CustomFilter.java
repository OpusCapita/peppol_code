package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by bambr on 16.28.12.
 */
public class CustomFilter extends ZuulFilter {
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
        return true;
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
        String remoteAddr = request.getRemoteAddr();
        String requestedService = extractRequestedService(request);
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

    private String extractRequestedService(HttpServletRequest request) {
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
