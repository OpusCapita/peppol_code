package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.eventing.destinations.mlr.MessageLevelResponseCreator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Checks whether we should report the message. If yes - creates the file in given directory.
 *
 * @author Sergejs.Roze
 */
@Component
public class MessageLevelResponseReporter {
    private final static Logger logger = LoggerFactory.getLogger(MessageLevelResponseReporter.class);

    private final MessageLevelResponseCreator creator;

    @Autowired
    public MessageLevelResponseReporter(@NotNull MessageLevelResponseCreator creator) {
        this.creator = creator;
    }

    public void process(@NotNull ContainerMessage cm) {

    }
}
