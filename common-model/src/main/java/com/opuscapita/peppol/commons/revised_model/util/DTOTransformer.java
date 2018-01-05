package com.opuscapita.peppol.commons.revised_model.util;

import com.opuscapita.peppol.commons.revised_model.Event;
import com.opuscapita.peppol.commons.revised_model.Attempt;
import com.opuscapita.peppol.commons.revised_model.Message;

import java.util.SortedSet;
import java.util.TreeSet;

public class DTOTransformer {
    public static Message fromEventingMessage(com.opuscapita.peppol.commons.events.Message internalMessage) {
        Message result = new Message();
        result.setId(internalMessage.getId());
        result.setCreated(internalMessage.getCreated());
        result.setAttempts(DTOTransformer.fromEventingAttempts(internalMessage.getAttempts(), result));
        result.setDocumentType(internalMessage.getDocumentType());
        result.setDocumentNumber(internalMessage.getDocumentNumber());
        result.setDocumentDate(internalMessage.getDocumentDate());
        result.setDueDate(internalMessage.getDueDate());
        return result;
    }

    private static SortedSet<Attempt> fromEventingAttempts(SortedSet<com.opuscapita.peppol.commons.events.Attempt> internalAttempts, Message message) {
        SortedSet<Attempt> result = new TreeSet<>();
        internalAttempts.stream().map(internalAttempt -> {
            Attempt attempt = new Attempt();
            attempt.setId(internalAttempt.getId());
            attempt.setFilename(internalAttempt.getFilename());
            attempt.setMessage(message);
            attempt.setEvents(DTOTransformer.fromEventingEvents(internalAttempt.getEvents(), attempt));
            return attempt;
        }).forEach(result::add);
        return result;
    }

    private static SortedSet<com.opuscapita.peppol.commons.revised_model.Event> fromEventingEvents(SortedSet<com.opuscapita.peppol.commons.events.Event> internalEvents, Attempt attempt) {
        SortedSet<Event> result = new TreeSet<>();
        internalEvents.stream().map(internalEvent -> {
            Event event = new Event();
            event.setId(internalEvent.getId());
            event.setDetails(internalEvent.getDetails());
            event.setSource(internalEvent.getSource());
            event.setStatus(internalEvent.getStatus());
            event.setTerminal(internalEvent.isTerminal());
            event.setAttempt(attempt);
            return event;
        }).forEach(result::add);
        return result;
    }
}
