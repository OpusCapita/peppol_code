package com.opuscapita.peppol.commons.template;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author Sergejs.Roze
 */
@Configuration
public class ServiceNowConfigurator {
    @Bean
    public ErrorHandler errorHandler(@NotNull ServiceNow serviceNowRest) {
        return new ErrorHandler(serviceNowRest);
    }

    @Bean
    public ServiceNowConfiguration serviceNowConfiguration(@NotNull Environment environment) {
        return new ServiceNowConfiguration(
                environment.getProperty("snc.rest.url"),
                environment.getProperty("snc.rest.username"),
                environment.getProperty("snc.rest.password"),
                environment.getProperty("snc.bsc"),
                environment.getProperty("snc.from"),
                environment.getProperty("snc.businessGroup"));
    }

    @Bean
    public ServiceNow serviceNowRest(@NotNull Environment environment) {
        return new ServiceNowREST(serviceNowConfiguration(environment));
    }

}
