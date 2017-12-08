package com.opuscapita.peppol.ui.portal.model;

import com.opuscapita.peppol.commons.revised_model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessagesRepository extends PagingAndSortingRepository<Message, String> {
    List<Message> findMessagesByInboundFalse();

    Page<Message> findMessagesByInboundFalse(Pageable pageable);

    List<Message> findMessagesByInboundTrue();

    Page<Message> findMessagesByInboundTrue(Pageable pageable);

    List<Message> findMessagesByInbound(boolean inbound);

    Page<Message> findMessagesByInbound(boolean inbound, Pageable pageable);

    Long countMessagesByInbound(boolean inbound);

    Long countMessagesByInboundAndCreatedBetween(boolean inbound, long start, long end);

    List<Message> findMessagesByInboundAndAttemptsEventsTerminal(boolean inbound, boolean terminal);

    Page<Message> findMessagesByInboundAndAttemptsEventsTerminal(boolean inbound, boolean terminal, Pageable pageable);

    Long countMessagesByInboundAndAttemptsEventsTerminal(boolean inbound, boolean terminal);

    Long countMessagesByInboundAndAttemptsEventsTerminal(boolean inbound, boolean terminal, long start, long end);

    Page<Message> findMessagesByInboundAndAttemptsEventsTerminalAndAttemptsEventsStatusContains(boolean inbound, boolean terminal, String status, Pageable pageable);

    Long countMessagesByInboundAndAttemptsEventsTerminalAndAttemptsEventsStatusContains(boolean inbound, boolean terminal, String status);

}
