package com.opuscapita.peppol.proxy;

import com.opuscapita.peppol.proxy.filters.pre.CustomFilter;
import com.opuscapita.peppol.proxy.filters.pre.FilterProperties;
import com.opuscapita.peppol.proxy.filters.pre.PreserveHostHeader;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

/**
 * Created by bambr on 16.19.12.
 */
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class ZuulProxyServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulProxyServerApplication.class, args);
    }

    @Bean
    public CustomFilter customFilter(FilterProperties filterProperties) {
        return new CustomFilter(filterProperties);
    }

	@Bean
	public PreserveHostHeader preserveHostHeader() {
	  return new PreserveHostHeader();
	}

    @Bean
    public TomcatEmbeddedServletContainerFactory containerFactory() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1));
        factory.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> connector.setMaxPostSize(-1));
        return factory;
    }
}

