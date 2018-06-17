package com.opuscapita.peppol.email.controller;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.email.prepare.AccessPointEmailCreator;
import com.opuscapita.peppol.email.prepare.CustomerEmailCreator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;


/**
 * Stores files for e-mail sender from received messages.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EmailController {
    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final CustomerEmailCreator customerEmailCreator;
    private final AccessPointEmailCreator accessPointEmailCreator;

    @Value("${peppol.email-notificator.status:''}")
    private String statusFile;
    // supported actions listed in EmailActions class
    @Value("${peppol.email-notificator.actions.inbound:nothing}")
    private String[] inboundActions;
    @Value("${peppol.email-notificator.actions.outbound:nothing}")
    private String[] outboundActions;

    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    public EmailController(@NotNull CustomerEmailCreator customerEmailCreator, @NotNull AccessPointEmailCreator accessPointEmailCreator) {
        this.customerEmailCreator = customerEmailCreator;
        this.accessPointEmailCreator = accessPointEmailCreator;
    }

    @PostConstruct
    public void postConstruct() {
        EmailActions.validate(Arrays.asList(outboundActions));
        EmailActions.validate(Arrays.asList(inboundActions));
        if (StringUtils.isNotBlank(statusFile)) {
            String json = new Gson().toJson(new Status(inboundActions, outboundActions));
            try (OutputStream outputStream = new FileOutputStream(statusFile)) {
                IOUtils.write(json, outputStream, Charset.forName("UTF-8"));
                logger.info("Created status file " + statusFile);
            } catch (Exception e) {
                logger.error("Failed to create status file: " + e.getMessage());
            }
        }

    }

    public void processMessage(@NotNull ContainerMessage cm) throws IOException {
        List<String> actions;
        if (cm.isInbound()) {
            actions = Arrays.asList(inboundActions);
        } else {
            actions = Arrays.asList(outboundActions);
        }

        if (actions.contains(EmailActions.NOTHING)) {
            logger.info("Skipping " + cm.getFileName() + " as action 'nothing' is set for it");
            return;
        }

        boolean createTicket = actions.contains(EmailActions.SNC_TICKET);
        if (actions.contains(EmailActions.EMAIL_AP)) {
            logger.info("Preparing access point e-mail for " + cm.getFileName());
            accessPointEmailCreator.create(cm, createTicket);
        }
        if (actions.contains(EmailActions.EMAIL_CUSTOMER)) {
            logger.info("Preparing customer e-mail for " + cm.getFileName());
            customerEmailCreator.create(cm, createTicket);
        }
    }

    // for unit tests
    void setOutboundActions(String... outboundActions) {
        this.outboundActions = outboundActions;
    }

}
