package com.opuscapita.peppol.email.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.email.prepare.AccessPointEmailCreator;
import com.opuscapita.peppol.email.prepare.CustomerEmailCreator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

}
