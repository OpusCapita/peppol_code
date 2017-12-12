package com.opuscapita.peppol.events.persistence;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opuscapita.peppol.commons.errors.PeppolFailureAnalyser;
import com.opuscapita.peppol.commons.model.PeppolEvent;
import com.opuscapita.peppol.commons.model.util.PeppolEventDeSerializer;
import com.opuscapita.peppol.events.persistence.amqp.EventQueueListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.opuscapita.peppol.events.persistence.model")
@ComponentScan(basePackages = {"com.opuscapita.peppol.events.persistence", "com.opuscapita.peppol.events.persistence.model", "com.opuscapita.peppol.commons"})
@EntityScan(basePackages = {"com.opuscapita.peppol.events.persistence.model", "com.opuscapita.peppol.commons.model"})
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableRetry
public class EventsPersistenceApplication {
    @Value("${peppol.events-persistence.queue.in.name}")
    private String queueName;

    public static void main(String[] args) {
        SpringApplication.run(EventsPersistenceApplication.class, args);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(5, new HashMap<Class<? extends Throwable>, Boolean>() {{
            put(ConnectException.class, true);
        }});
        retryPolicy.setMaxAttempts(5);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(10000); // 10 seconds

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }

    @Bean
    public CacheManager cacheManager() {
        String specAsString = "initialCapacity=100,maximumSize=500,expireAfterAccess=5m,recordStats";
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(false); //can happen if you get a value from a @Cachable that returns null
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(150)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .weakKeys()
                .recordStats();
    }

    @Bean
    public FailureAnalyzer failureAnalyzer() {
        return new PeppolFailureAnalyser();
    }

    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setPrefetchCount(10);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    @ConditionalOnProperty("spring.rabbitmq.host")
    MessageListenerAdapter listenerAdapter(EventQueueListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder().disableHtmlEscaping().registerTypeAdapter(PeppolEvent.class, new PeppolEventDeSerializer()).create();
    }

}
