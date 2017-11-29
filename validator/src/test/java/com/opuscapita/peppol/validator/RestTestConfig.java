package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by bambr on 16.17.10.
 */
/*@Configuration
@ComponentScan(basePackages = {"com.opuscapita.peppol"})*/
public class RestTestConfig {
    public RestTestConfig() throws URISyntaxException {
        System.setProperty("peppol.component.name", "validator");
        System.setProperty("server.port", "0");
        System.setProperty("peppol.validator.sbdh.xsd", getAbsolutePathToResource("sbdh_artifacts/StandardBusinessDocumentHeader.xsd"));
        System.setProperty("peppol.validator.svefaktura1.schematron.path", getAbsolutePathToResource("svefaktura1_artifacts/rules_svefaktura_2016-09-01.xsl"));
        System.setProperty("peppol.validator.svefaktura1.xsd.path", getAbsolutePathToResource("svefaktura1_artifacts/maindoc/SFTI-BasicInvoice-1.0.xsd"));
        System.setProperty("peppol.validator.svefaktura1.schematron.enabled", "false");
        System.setProperty("peppol.validation.artifacts.difi.path", getAbsolutePathToResource("difi_artifacts/"));
        System.setProperty("peppol.validation.artifacts.si.path", getAbsolutePathToResource("difi_artifacts/"));
        System.setProperty("peppol.validation.artifacts.at.path", getAbsolutePathToResource("difi_artifacts/"));
        System.setProperty("peppol.validation.consume-queue", "validator");
        System.setProperty("peppol.email-notificator.queue.in.name", "email");
        System.setProperty("spring.cloud.config.discovery.enabled", "false");


    }

    private String getAbsolutePathToResource(String relativePathToResource) throws URISyntaxException {
        return Paths.get(RestTestConfig.class.getClassLoader().getResource(relativePathToResource).toURI()).toFile().getAbsolutePath();
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
        return new ServiceNow() {
            @Override
            public void insert(SncEntity sncEntity) throws IOException {
                System.out.println("Inserted incident: " + sncEntity);
            }
        };
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler(serviceNowRest());
    }
}
