package com.opuscapita.peppol.eventing.revised;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.revised_model.Attempt;
import com.opuscapita.peppol.commons.revised_model.Event;
import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.revised_model.util.DTOTransformer;
import com.opuscapita.peppol.eventing.revised.repositories.MessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.SortedSet;
import java.util.TreeSet;

/*@Component*/
public class MessageAttemptEventReporter {
    MessagesRepository messagesRepository;

    @Autowired
    public MessageAttemptEventReporter(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public void process(ContainerMessage containerMessage) {
        com.opuscapita.peppol.commons.events.Message internalMessage = containerMessage.getProcessingInfo().getEventingMessage();
        Message message = DTOTransformer.fromEventingMessage(internalMessage);
        messagesRepository.save(message);
    }
}
