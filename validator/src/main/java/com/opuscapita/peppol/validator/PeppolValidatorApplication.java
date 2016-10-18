package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.opuscapita.peppol")
public class PeppolValidatorApplication {
    @Value("${peppol.validator.svefaktura1.schematron.path}")
    String svefaktur1SchematronXslPath;
    @Value("${peppol.validator.svefaktura1.xsd.path}")
    String svefaktura1XsdPath;
    @Value("${peppol.validator.svefaktura1.schematron.enabled}")
    Boolean svefaktura1SchematronValidationEnabled;
    @Value("${peppol.validation.artifacts.difi.path}")
    String difiValidationArtifactsPath;
    @Value("${peppol.validation.artifacts.si.path}")
    String simplerInvoicingValidationArtifactsPath;
    @Value("${peppol.validation.artifacts.at.path}")
    String austrianValidationArtifactsPath;
    @Value("${peppol.validation.consume-queue}")
    private String queueName;
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        try {
            SpringApplication.run(PeppolValidatorApplication.class, args);
        } catch (Exception e) {
            //Failed to launch the application
            //Try snc stuff? :)
            e.printStackTrace();
        }
    }

    @Bean
    public Svefaktura1ValidatorConfig svefaktura1ValidatorConfig() {
        return new Svefaktura1ValidatorConfig(svefaktura1SchematronValidationEnabled, svefaktur1SchematronXslPath, svefaktura1XsdPath);
    }

    @Bean
    public DifiValidatorConfig difiValidatorConfig() {
        return new DifiValidatorConfig(difiValidationArtifactsPath);
    }

    @Bean
    public DifiValidatorConfig simplerInvoicingValidatorConfig() {
        return new DifiValidatorConfig(simplerInvoicingValidationArtifactsPath);
    }

    @Bean
    public DifiValidatorConfig austrianValidatorConfig() {
        return new DifiValidatorConfig(austrianValidationArtifactsPath);
    }


    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    ServiceNowConfiguration serviceNowConfiguration() {
        return new ServiceNowConfiguration(
                environment.getProperty("snc.rest.url"),
                environment.getProperty("snc.rest.username"),
                environment.getProperty("snc.rest.password"),
                environment.getProperty("snc.bsc"),
                environment.getProperty("snc.from"),
                environment.getProperty("snc.businessGroup"));
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNowREST(serviceNowConfiguration());
    }

}
