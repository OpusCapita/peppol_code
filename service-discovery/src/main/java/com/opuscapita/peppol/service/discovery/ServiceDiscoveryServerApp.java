package com.opuscapita.peppol.service.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by bambr on 16.5.12.
 */
@SpringBootApplication
@EnableEurekaServer
public class ServiceDiscoveryServerApp {
    public static void main(String[] args) {
        SpringApplication.run(ServiceDiscoveryServerApp.class, args);
    }
}
