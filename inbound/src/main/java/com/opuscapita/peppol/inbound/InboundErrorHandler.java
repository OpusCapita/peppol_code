package com.opuscapita.peppol.inbound;

import com.opuscapita.commons.servicenow.ServiceNowConfiguration;
import com.opuscapita.commons.servicenow.ServiceNowREST;
import com.opuscapita.commons.servicenow.SncEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Sergejs.Roze
 */
public class InboundErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(InboundErrorHandler.class);

    private final ServiceNowREST serviceNow;

    public InboundErrorHandler(@NotNull InboundProperties properties) {
        InboundProperties p = properties;
        ServiceNowConfiguration cfg = new ServiceNowConfiguration(
                p.getProperty("snc.rest.url"),
                p.getProperty("snc.rest.username"),
                p.getProperty("snc.rest.password"),
                p.getProperty("snc.bsc"),
                p.getProperty("snc.from"),
                p.getProperty("snc.businessGroup"));
        serviceNow = new ServiceNowREST(cfg);
    }

    public void report(@NotNull String message, @NotNull String detailed, @Nullable String customerId) {
        SncEntity report = new SncEntity(message, detailed, UUID.randomUUID().toString(), customerId == null ? "Peppol inbound" : customerId, 0);

        try {
            serviceNow.insert(report);
        } catch (IOException e) {
            logger.error("Failed to open ServiceNow ticket about " + message + " (" + detailed + ")", e);
        }
    }

}
