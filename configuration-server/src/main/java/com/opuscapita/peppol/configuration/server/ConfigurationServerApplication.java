package com.opuscapita.peppol.configuration.server;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@EnableConfigServer
@SpringBootApplication
public class ConfigurationServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ConfigurationServerApplication.class).properties("spring.config.name=configserver").run(args);
    }
}
