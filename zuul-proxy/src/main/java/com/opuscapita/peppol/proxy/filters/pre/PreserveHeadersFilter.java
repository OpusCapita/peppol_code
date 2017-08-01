package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.opuscapita.peppol.proxy.filters.pre.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final Logger logger = LoggerFactory.getLogger(PreserveHeadersFilter.class);
    private final PreserveHeaderFilterProperties preserveHeaderFilterProperties;
    private final String zuulServletPath;

    public PreserveHeadersFilter(PreserveHeaderFilterProperties preserveHeaderFilterProperties, String zuulServletPath) {
        this.preserveHeaderFilterProperties = preserveHeaderFilterProperties;
        this.zuulServletPath = zuulServletPath;
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
        if (!headersToPreserve.contains(HEADER_HOST)) {
            headersToPreserve.add(HEADER_HOST);
        }
        headersToPreserve.forEach(header -> {
            logger.debug("Header: " + header + " does have value -> " + headerHasValue(ctx, header));
            if (headerHasValue(ctx, header)) {
                String value = ctx.getRequest().getHeaders(header).nextElement();
                ctx.getZuulRequestHeaders().put(header, value);
                logger.debug("Stored to zuul request headers [" + header + ":" + value + "]");
            }
        });
        /*String host = ctx.getRequest().getHeaders(HEADER_HOST).nextElement();
        ctx.getZuulRequestHeaders().put(HEADER_HOST, host);*/
        ctx.getZuulRequestHeaders().put(REQUEST_SERVICE, RequestUtils.extractRequestedService(ctx.getRequest(), zuulServletPath));
        return null;
    }

    private boolean headerHasValue(RequestContext ctx, String header) {
        return ctx.getRequest().getHeaders(header).hasMoreElements();
    }
}
