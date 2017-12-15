package com.opuscapita.peppol.proxy;

import com.opuscapita.peppol.proxy.filters.pre.AccessCheckFilter;
import com.opuscapita.peppol.proxy.filters.pre.AccessFilterProperties;
import com.opuscapita.peppol.proxy.filters.pre.PreserveHeaderFilterProperties;
import com.opuscapita.peppol.proxy.filters.pre.PreserveHeadersFilter;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Created by bambr on 16.19.12.
 */
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ZuulProxyServerApplication {
    private final static Logger logger = LoggerFactory.getLogger(ZuulProxyServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ZuulProxyServerApplication.class, args);
    }

    @Bean
    public AccessCheckFilter customFilter(AccessFilterProperties accessFilterProperties, @Value("${zuul.servletPath}") String zuulServletPath) {
        logger.info("zuul.servletPath: " + zuulServletPath);
        return new AccessCheckFilter(accessFilterProperties, zuulServletPath);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PreserveHeadersFilter preserveHostHeader(PreserveHeaderFilterProperties preserveHeaderFilterProperties, @Value("${zuul.servletPath}") String zuulServletPath) {
        logger.info("zuul.servletPath: " + zuulServletPath);
        return new PreserveHeadersFilter(preserveHeaderFilterProperties, zuulServletPath);
    }

    @Bean
    public TomcatEmbeddedServletContainerFactory containerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1));
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> connector.setMaxPostSize(-1));
        return factory;
    }
}

