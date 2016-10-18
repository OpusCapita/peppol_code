package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * Created by bambr on 16.17.10.
 */
@Configuration
@ComponentScan(basePackages = {"com.opuscapita.peppol"}, excludeFilters = @ComponentScan.Filter(value = PeppolValidatorApplication.class, type = FilterType.ASSIGNABLE_TYPE))
public class TestConfig {

    public TestConfig() throws URISyntaxException {
        System.setProperty("peppol.validator.sbdh.xsd", getAbsolutePathToResource("sbdh_artifacts/StandardBusinessDocumentHeader.xsd"));
    }

    private String getAbsolutePathToResource(String relativePathToResource) throws URISyntaxException {
        return Paths.get(TestConfig.class.getClassLoader().getResource(relativePathToResource).toURI()).toFile().getAbsolutePath();
    }

    @Bean
    public Svefaktura1ValidatorConfig svefaktura1ValidatorConfig() throws URISyntaxException {
        return new Svefaktura1ValidatorConfig(true, getAbsolutePathToResource("svefaktura1_artifacts/rules_svefaktura_2016-08-16.xsl"), getAbsolutePathToResource("svefaktura1_artifacts/maindoc/SFTI-BasicInvoice-1.0.xsd"));
    }

    @Bean
    public DifiValidatorConfig difiValidatorConfig() throws URISyntaxException {
        return new DifiValidatorConfig(getAbsolutePathToResource("difi_artifacts/"));
    }

    @Bean
    public DifiValidatorConfig simplerInvoicingValidatorConfig() throws URISyntaxException {
        return new DifiValidatorConfig(getAbsolutePathToResource("simpler_invoicing_artifacts/"));
    }

    @Bean
    public DifiValidatorConfig austrianValidatorConfig() throws URISyntaxException {
        return new DifiValidatorConfig(getAbsolutePathToResource("austrian_artifacts/"));
    }


    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return null;
    }
}