package com.opuscapita.peppol.email.send;

import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.email.model.CombinedEmail;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketsCreator {
    private final static Logger logger = LoggerFactory.getLogger(TicketsCreator.class);

    private final ErrorHandler errorHandler;

    @Autowired
    public TicketsCreator(@NotNull ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @SuppressWarnings("WeakerAccess")
    public void createTicket(@NotNull CombinedEmail combinedEmail, @NotNull String combinedEmailFileName) {
        logger.info("Creating ticket about successfully sent e-mail for " + combinedEmailFileName);
        errorHandler.reportWithoutContainerMessage(
                combinedEmail.getRecipient().getId(),
                null,
                combinedEmail.getTicketHeader(),
                combinedEmailFileName,
                null,
                combinedEmail.getTicketBody());
    }

}
