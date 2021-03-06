package com.opuscapita.peppol.eventing.revised;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.commons.revised_model.util.DTOTransformer;
import com.opuscapita.peppol.eventing.revised.repositories.MessagesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageAttemptEventReporter {
    private final static Logger logger = LoggerFactory.getLogger(MessageAttemptEventReporter.class);
    MessagesRepository messagesRepository;

    @Value("${peppol.eventing.attempts.storing.enabled:false}")
    Boolean storingEnabled;

    @Autowired
    public MessageAttemptEventReporter(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public void process(ContainerMessage cm) {
        if(!storingEnabled) {
            return;
        }
        com.opuscapita.peppol.commons.events.Message internalMessage = cm.getProcessingInfo().getEventingMessage();

        boolean isNull = internalMessage == null;
        if (isNull) {
            logger.info("File with null eventing message: " + cm.toLog());
            logger.info("Source: " + cm.getProcessingInfo().getSource());
        }

        Message message = DTOTransformer.fromEventingMessage(internalMessage);
        //TODO: check if inversion of sender/recipient is needed based on direction in/out
        message.setRecipient(cm.getDocumentInfo().getRecipientId());
        message.setSender(cm.getDocumentInfo().getSenderId());
        message.setInbound(cm.isInbound());
        messagesRepository.save(message);
        logger.info("Saved message: " + message);
    }
}
