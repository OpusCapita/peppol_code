package com.opuscapita.peppol.eventing.revised;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.commons.revised_model.util.DTOTransformer;
import com.opuscapita.peppol.eventing.revised.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageAttemptEventReporter {
    MessagesRepository messagesRepository;

    @Autowired
    public MessageAttemptEventReporter(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public void process(ContainerMessage containerMessage) {
        com.opuscapita.peppol.commons.events.Message internalMessage = containerMessage.getProcessingInfo().getEventingMessage();
        Message message = DTOTransformer.fromEventingMessage(internalMessage);
        //TODO: check if inversion of sender/recipient is needed based on direction in/out
        message.setRecipient(containerMessage.getDocumentInfo().getRecipientId());
        message.setSender(containerMessage.getDocumentInfo().getSenderId());
        message.setInbound(containerMessage.isInbound());
        messagesRepository.save(message);
    }
}
