package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by bambr on 16.17.10.
 */
@Configuration
@ComponentScan(basePackages = {"com.opuscapita.peppol"}, excludeFilters = @ComponentScan.Filter(value = {PeppolValidatorApplication.class}, type = FilterType.ASSIGNABLE_TYPE))
public class TestConfig {
    @Autowired
    private Environment environment;

    public TestConfig() throws URISyntaxException {
        System.setProperty("peppol.validator.sbdh.xsd", getAbsolutePathToResource("sbdh_artifacts/StandardBusinessDocumentHeader.xsd"));
    }

    private String getAbsolutePathToResource(String relativePathToResource) throws URISyntaxException {
        return Paths.get(TestConfig.class.getClassLoader().getResource(relativePathToResource).toURI()).toFile().getAbsolutePath();
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
        return new ErrorHandler(serviceNowRest(), environment);
    }
}
