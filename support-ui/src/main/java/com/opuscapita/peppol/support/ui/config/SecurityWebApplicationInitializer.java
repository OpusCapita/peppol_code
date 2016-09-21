package com.opuscapita.peppol.support.ui.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.ServletContext;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        super.beforeSpringSecurityFilterChain(servletContext);
    }
}
