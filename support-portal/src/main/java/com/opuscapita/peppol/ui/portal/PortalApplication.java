package com.opuscapita.peppol.ui.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication
@EnableSpringHttpSession
@EnableJpaRepositories(basePackages = {"com.opuscapita.peppol.ui.portal.model"})
@EntityScan(basePackages = {"com.opuscapita.peppol.commons.model", "com.opuscapita.peppol.commons.revised_model"})
@EnableDiscoveryClient
public class PortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalApplication.class, args);
	}
}
