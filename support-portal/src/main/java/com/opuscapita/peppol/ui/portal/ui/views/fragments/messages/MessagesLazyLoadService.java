package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.ui.portal.model.MessagesRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessagesLazyLoadService {
    private final MessagesRepository messagesRepository;

    @Autowired
    public MessagesLazyLoadService(MessagesRepository messagesRepository) {
        this.messagesRepository = messagesRepository;
    }

    public List<Message> findAll(int offset, int limit, Map<String, Boolean> sortOrders) {
        PageRequest pageRequest = composePageRequest(offset, limit, sortOrders);
        List<Message> items = messagesRepository.findAll(pageRequest).getContent();
        return items.subList(offset % limit, items.size());
    }

    @NotNull
    protected PageRequest composePageRequest(int offset, int limit, Map<String, Boolean> sortOrders) {
        int page = offset / limit;
        List<Sort.Order> orders = sortOrders.entrySet().stream()
                .map(e -> new Sort.Order(e.getValue() ? Sort.Direction.ASC : Sort.Direction.DESC, e.getKey()))
                .collect(Collectors.toList());

        return new PageRequest(page, limit, orders.isEmpty() ? null : new Sort(orders));
    }

    public List<Message> findByInbound(boolean inbound, int offset, int limit, Map<String, Boolean> sortOrders) {
        PageRequest pageRequest = composePageRequest(offset, limit, sortOrders);
        List<Message> items = messagesRepository.findMessagesByInbound(inbound, pageRequest).getContent();
        return items.subList(offset % limit, items.size());
    }

    public List<Message> findByInboundAndTerminal(boolean inbound, boolean terminal, int offset, int limit, Map<String, Boolean> sortOrders) {
        PageRequest pageRequest = composePageRequest(offset, limit, sortOrders);
        List<Message> items = messagesRepository.findMessagesByInboundAndAttemptsEventsTerminal(inbound, terminal, pageRequest).getContent();
        return items.subList(offset % limit, items.size());
    }


    public Integer count() {
        return Math.toIntExact(messagesRepository.count());
    }

    public Integer countByInbound(boolean inbound) {
        return Math.toIntExact(messagesRepository.countMessagesByInbound(inbound));
    }

    public Integer countByInboundAndTerminal(boolean inbound, boolean terminal) {
        return Math.toIntExact(messagesRepository.countMessagesByInboundAndAttemptsEventsTerminal(inbound, terminal));
    }


    public List<Message> findByInboundAndTerminalAndStatusContains(boolean inbound, boolean terminal, String keyWord, int offset, int limit, Map<String, Boolean> sortOrders) {
        PageRequest pageRequest = composePageRequest(offset, limit, sortOrders);
        List<Message> items = messagesRepository.findMessagesByInboundAndAttemptsEventsTerminalAndAttemptsEventsStatusContains(inbound, terminal, keyWord, pageRequest).getContent();
        return items;
    }

    public int countByInboundAndTerminalAndStatusContains(boolean inbound, boolean terminal, String keyWord) {
        return Math.toIntExact(messagesRepository.countMessagesByInboundAndAttemptsEventsTerminalAndAttemptsEventsStatusContains(inbound, terminal, keyWord));
    }
}
