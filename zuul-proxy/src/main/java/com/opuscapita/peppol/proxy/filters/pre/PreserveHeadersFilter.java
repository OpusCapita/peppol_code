package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opuscapita.peppol.proxy.filters.pre.util.RequestUtils;

import java.util.List;

/**
 * Creaded by didried1
 * Filter preserves host header as ProxyPreserveHost does
 * http://httpd.apache.org/docs/2.2/mod/mod_proxy.html#proxypreservehost
 * <p>
 * This enables redirections in backend applications
 */

public class PreserveHeadersFilter extends ZuulFilter {
    public static final String HEADER_HOST = "Host";
    public static final String REQUEST_SERVICE = "Service";
    private final PreserveHeaderFilterProperties preserveHeaderFilterProperties;

    public PreserveHeadersFilter(PreserveHeaderFilterProperties preserveHeaderFilterProperties) {
        this.preserveHeaderFilterProperties = preserveHeaderFilterProperties;
    }

    public String filterType() {
        return "pre";
    }

    public int filterOrder() {
        return 0;
    }

    public boolean shouldFilter() {
        /*RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getRequest().getHeaders(HEADER_HOST).hasMoreElements();*/
        return true;
    }

    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        List<String> headersToPreserve = preserveHeaderFilterProperties.getHeadersToPreserve();
        headersToPreserve.forEach(header -> {
            if (ctx.getRequest().getHeaders(header).hasMoreElements()) {
                String value = ctx.getRequest().getHeaders(header).nextElement();
                ctx.getZuulRequestHeaders().put(header, value);
            }
        });
        /*String host = ctx.getRequest().getHeaders(HEADER_HOST).nextElement();
        ctx.getZuulRequestHeaders().put(HEADER_HOST, host);*/
        ctx.getZuulRequestHeaders().put(REQUEST_SERVICE, RequestUtils.extractRequestedService(ctx.getRequest()));
        return null;
    }
}
