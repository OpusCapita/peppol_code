package com.opuscapita.peppol.email.sender;

import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorsList;
import com.opuscapita.peppol.commons.mq.RabbitMq;
import com.opuscapita.peppol.email.EmailNotificatorApp;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@Configuration
@PropertySource("classpath:/application.yml")
@ComponentScan(basePackages = {"com.opuscapita.peppol.email", "com.opuscapita.peppol.commons.errors.oxalis"}, excludeFilters = @ComponentScan.Filter(value = {EmailNotificatorApp.class, RabbitMq.class}, type = FilterType.ASSIGNABLE_TYPE))
public class TestConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        System.out.println(yaml.getObject());
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }

    @Bean
    public ErrorHandler errorHandler() {
        return mock(ErrorHandler.class);
    }

    @Bean
    public DirectoryChecker directoryChecker(EmailSender emailSender, ErrorHandler errorHandler) {
        return new DirectoryChecker(emailSender, errorHandler);
    }

    @Bean
    public OxalisErrorsList oxalisErrorsList() {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        OxalisErrorsList oxalisErrorsList = new OxalisErrorsList();
        return oxalisErrorsList;
    }
}
