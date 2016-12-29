package com.opuscapita.peppol.proxy.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 *  Creaded by didried1
 *  Filter preserves host header as ProxyPreserveHost does
 *  http://httpd.apache.org/docs/2.2/mod/mod_proxy.html#proxypreservehost
 *
 *  This enables redirections in backend applications 
 */

public class PreserveHostHeader extends ZuulFilter {
  public static final String HEADER_HOST = "Host";

  public String filterType() {
    return "pre";
  }

  public int filterOrder() {
    return 0;
  }

  public boolean shouldFilter() {
    RequestContext ctx = RequestContext.getCurrentContext();
    return ctx.getRequest().getHeaders(HEADER_HOST).hasMoreElements();
  }

  public Object run() {
    RequestContext ctx = RequestContext.getCurrentContext();
    String host = ctx.getRequest().getHeaders(HEADER_HOST).nextElement();
    ctx.getZuulRequestHeaders().put(HEADER_HOST, host);

    return null;
  }
}
