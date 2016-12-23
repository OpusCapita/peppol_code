package com.opuscapita.peppol.transport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Reads file from local disk and stores in designated message queue.
 *
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.commons", "com.opuscapita.peppol.transport"})
@EnableScheduling
@EnableDiscoveryClient
public class FileToMessageQueueApp {
    public static void main(String[] args) {
        SpringApplication.run(FileToMessageQueueApp.class, args);
    }

}
