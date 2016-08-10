package com.opuscapita.peppol.validator;

import com.google.gson.Gson;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(scanBasePackages = "com.opuscapita.peppol")
public class PeppolValidatorApplication {
    @Value("${amqp.queueName}")
    private String queueName;



    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        try {
            SpringApplication.run(PeppolValidatorApplication.class, args);
        } catch (Exception e) {
            //Failed to launch the application
            //Try snc stuff? :)
        }
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
