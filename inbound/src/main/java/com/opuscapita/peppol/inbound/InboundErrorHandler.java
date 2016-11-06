package com.opuscapita.peppol.inbound;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.ServiceNowConfigurator;
import org.jetbrains.annotations.NotNull;
import org.springframework.mock.env.MockEnvironment;

/**
 * @author Sergejs.Roze
 */
public class InboundErrorHandler {
    private final ErrorHandler errorHandler;

    public InboundErrorHandler(@NotNull InboundProperties properties) {
        MockEnvironment environment = new MockEnvironment();
        for (Object key : properties.getProperties().keySet()) {
            String value = properties.getProperty((String) key);
            environment.setProperty((String) key, value);
        }

        ServiceNowConfigurator configurator = new ServiceNowConfigurator();
        ServiceNow serviceNow = configurator.serviceNowRest(environment);
        errorHandler = new ErrorHandler(serviceNow, environment);
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

}
