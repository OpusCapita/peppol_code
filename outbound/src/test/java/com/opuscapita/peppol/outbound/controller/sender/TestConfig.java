package com.opuscapita.peppol.outbound.controller.sender;

import com.opuscapita.peppol.commons.container.xml.DocumentTemplates;
import com.opuscapita.peppol.outbound.OutboundApp;
import com.opuscapita.peppol.outbound.controller.OutboundController;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import javax.xml.parsers.SAXParserFactory;

@Configuration
@PropertySource("classpath:/application-document_types.yml")
@ComponentScan(basePackages = {/*"com.opuscapita.peppol", */"com.opuscapita.peppol.commons.container.xml"}, excludeFilters = @ComponentScan.Filter(value = {RealSender.class, OutboundController.class, OutboundApp.class}, type = FilterType.ASSIGNABLE_TYPE))
public class TestConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-document_types.yml"));
        System.out.println(yaml.getObject());
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public static SAXParserFactory saxFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    @Bean
    public static DocumentTemplates templates() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application-document_types.yml"));
        DocumentTemplates templates = new DocumentTemplates();

        return templates;
    }

}
