package com.opuscapita.peppol.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import com.opuscapita.peppol.proxy.filters.pre.PreserveHostHeader;

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
	public PreserveHostHeader preserveHostHeader() {
	  return new PreserveHostHeader();
	}
}

