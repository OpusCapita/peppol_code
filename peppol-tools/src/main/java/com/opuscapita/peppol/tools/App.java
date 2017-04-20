package com.opuscapita.peppol.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by bambr on 17.19.4.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        try {
            SpringApplication.run(App.class, args);
        } catch (Exception e) {
            //Failed to launch the application
            //Try snc stuff? :)
            e.printStackTrace();
        }
    }
}
