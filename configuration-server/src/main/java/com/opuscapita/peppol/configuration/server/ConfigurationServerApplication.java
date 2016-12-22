package com.opuscapita.peppol.configuration.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableAutoConfiguration
@EnableConfigServer
@EnableDiscoveryClient
@SpringBootApplication
public class ConfigurationServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigurationServerApplication.class).properties("spring.config.name=configserver").run(args);
    }
}
