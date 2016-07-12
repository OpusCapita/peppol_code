package com.opuscapita.peppol.commons;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Somehow the Spring Boot requires that there is an application to run.
 * Let'em run this one. Something useless.
 *
 * @author Sergejs.Roze
 */
@SpringBootApplication
public class Ignore implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {}

    public static void main(String[] args) {
        SpringApplication.run(Ignore.class, args);
    }

}
