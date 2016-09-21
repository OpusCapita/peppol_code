package com.opuscapita.peppol.support.ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@ComponentScan(basePackages = {"com.opuscapita.peppol.support.ui.controller"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, value = {WebPackageFilter.class})})
@EnableWebMvc
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    /* MVC configuration*/
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/images/**").addResourceLocations("/images/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/js/").setCachePeriod(31556926);
        registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/").setCachePeriod(31556926);
        registry.addResourceHandler("/views/**").addResourceLocations("/views/").setCachePeriod(31556926);
        registry.addResourceHandler("/template/**").addResourceLocations("/template/").setCachePeriod(31556926);
        registry.addResourceHandler("/ng-table/**").addResourceLocations("/ng-table/").setCachePeriod(31556926);
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/").setCachePeriod(31556926);
    }

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }
}
