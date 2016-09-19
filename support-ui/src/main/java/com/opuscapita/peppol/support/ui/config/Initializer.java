package com.opuscapita.peppol.support.ui.config;

import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
public class Initializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    public Initializer() {
        System.out.println("Initializer starting. PEPPOL_CONFIG_PATH = " + System.getenv("PEPPOL_CONFIG_PATH"));
        Map<String, String> envs = System.getenv();
        for (Map.Entry entry : envs.entrySet()) {
            System.out.println("Environment variable: " + entry.getKey() + "=" + entry.getValue());
        }
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{RootConfiguration.class, SecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);
        servletContext.addListener(new HttpSessionEventPublisher());
        servletContext.getSessionCookieConfig().setMaxAge(525600);
    }

    @Override
    protected Filter[] getServletFilters() {
        OpenSessionInViewFilter hibernateFilter = new OpenSessionInViewFilter();
        hibernateFilter.setSessionFactoryBeanName("sessionFactory");
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");

        return new Filter[]{characterEncodingFilter, hibernateFilter};
    }
}