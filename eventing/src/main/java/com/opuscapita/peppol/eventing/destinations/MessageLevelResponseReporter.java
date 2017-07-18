package com.opuscapita.peppol.eventing.destinations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
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

    // only messages about errors and successfull delivery must get through
    void process(@NotNull ContainerMessage cm) {
        // nothing to do if there is no info about the file
        if (cm.getDocumentInfo() == null || cm.getProcessingInfo() == null) {
            return;
        }

        ProcessingInfo pi = cm.getProcessingInfo();
        DocumentInfo di = cm.getDocumentInfo();

        // report errors
        if (di.getArchetype() == Archetype.INVALID) {
            creator.reportError(cm);
            return;
        }

        // report successfull end of the flow
        if (pi.getCurrentEndpoint().getType() == ProcessType.OUT_PEPPOL_FINAL) {
            if (di.getErrors().isEmpty()) {
                creator.reportSuccess(cm);
            } else {
                creator.reportError(cm);
            }
        }
    }
}
