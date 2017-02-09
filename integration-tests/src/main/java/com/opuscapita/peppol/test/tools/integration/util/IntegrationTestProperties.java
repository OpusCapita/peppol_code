package com.opuscapita.peppol.test.tools.integration.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by bambr on 16.28.12.
 */
@ConfigurationProperties(prefix = "integration.test")
@Component
@RefreshScope
public class IntegrationTestProperties {
    private List<String> queues;

    private Rabbitmq rabbitmq;

    public IntegrationTestProperties() {
    }

    public IntegrationTestProperties(List<String> queues) {
        this.queues = queues;
    }

    public List<String> getQueues() {
        return queues;
    }

    public void setQueues(List<String> queues) {
        this.queues = queues;
    }

    public Rabbitmq getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(Rabbitmq rabbitmq) {
        this.rabbitmq = rabbitmq;
    }

    public static class Rabbitmq{
        public String host;
        public int port;
        public String username;
        public String password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
