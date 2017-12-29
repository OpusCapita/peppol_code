package com.opuscapita.peppol.validator.controller;

import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.validator.ValidationController;
import com.opuscapita.peppol.validator.controller.body.BodyValidator;
import com.opuscapita.peppol.validator.controller.util.DocumentSplitter;
import com.opuscapita.peppol.validator.controller.xsd.HeaderValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;
import java.util.Arrays;

import static org.mockito.Mockito.mock;

/**
 * @author Sergejs.Roze
 */
@Configuration
@ComponentScan(basePackages = { "com.opuscapita.peppol.validator.controller", "com.opuscapita.peppol.commons.container" })
@EnableConfigurationProperties
@EnableCaching
public class ValidationControllerTestConfig {

    @Bean
    public TransformerFactory transformerFactory() {
        return TransformerFactory.newInstance();
    }

    @Bean
    public SAXParserFactory saxParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    @Bean
    public XMLInputFactory xmlInputFactory() {
        return XMLInputFactory.newFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public MessageQueue messageQueue() {
        return mock(MessageQueue.class);
    }

    @Bean
    public ErrorHandler errorHandler() {
        return mock(ErrorHandler.class);
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("xsd"),
                new ConcurrentMapCache("xsl")
        ));
        return cacheManager;
    }

    @Bean
    public SchemaFactory schemaFactory() {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    @Bean
    @ConditionalOnMissingBean
    public ValidationController validationController(@NotNull DocumentSplitter documentSplitter, @NotNull HeaderValidator headerValidator,
                                                     @NotNull BodyValidator bodyValidator) {
        return new ValidationControllerImpl(documentSplitter, headerValidator, bodyValidator);
    }

}
