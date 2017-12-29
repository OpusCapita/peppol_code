package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.template.AbstractQueueListener;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.SAXParserFactory;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import static org.mockito.Mockito.mock;

/**
 * Created by bambr on 16.17.10.
 */
@Configuration
@ComponentScan(basePackages = { "com.opuscapita.peppol.validator.validations",
                                "com.opuscapita.peppol.commons.container"})
@EnableConfigurationProperties
public class DifiTestConfig {
    public DifiTestConfig() throws URISyntaxException {
        System.setProperty("peppol.validator.sbdh.xsd", getAbsolutePathToResource("sbdh_artifacts/StandardBusinessDocumentHeader.xsd"));
    }

    @SuppressWarnings("ConstantConditions")
    private String getAbsolutePathToResource(String relativePathToResource) throws URISyntaxException {
        return Paths.get(DifiTestConfig.class.getClassLoader().getResource(relativePathToResource).toURI()).toFile().getAbsolutePath();
    }

    @Bean
    public Svefaktura1ValidatorConfig svefaktura1ValidatorConfig() throws URISyntaxException {
        return new Svefaktura1ValidatorConfig(true, getAbsolutePathToResource("svefaktura1_artifacts/rules_svefaktura_2016-09-01.xsl"), getAbsolutePathToResource("svefaktura1_artifacts/maindoc/SFTI-BasicInvoice-1.0.xsd"));
    }

    @Bean
    public DifiValidatorConfig difiValidatorConfig() throws URISyntaxException {
        return new DifiValidatorConfig(getAbsolutePathToResource("difi_artifacts/"));
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return sncEntity -> System.out.println("Inserted incident: " + sncEntity);
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler(serviceNowRest());
    }

    @Bean
    public AbstractQueueListener abstractQueueListener() {
        return mock(AbstractQueueListener.class);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return mock(ConnectionFactory.class);
    }

    @Bean
    public SAXParserFactory saxParserFactory() {
        return SAXParserFactory.newInstance();
    }

    @Bean
    public MessageQueue messageQueue() {
        return mock(MessageQueue.class);
    }
}
