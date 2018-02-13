package com.opuscapita.peppol.ui.portal.ui.views.fragments.messages;

import com.opuscapita.peppol.commons.revised_model.Message;
import com.opuscapita.peppol.commons.revised_model.QMessage;
import com.opuscapita.peppol.ui.portal.model.MessagesRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
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
        if (sortOrders.size() == 0) {
            sortOrders.put("created", false);
        }
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

    public List<Message> findByFilter(Map<String, String> filterFields, int offset, int limit, Map<String, Boolean> sortOrders) {
        PageRequest pageRequest = composePageRequest(offset, limit, sortOrders);
        FilterHolder filterHolder = fillFilterHolder(filterFields);
        return messagesRepository.findAll(filterHolder.getFilter(), pageRequest).getContent();
    }

    public List<String> findDocumentTypes() {
        return messagesRepository.getDocumentTypes();
    }

    @NotNull
    protected FilterHolder fillFilterHolder(Map<String, String> filterFields) {
        QMessage message = QMessage.message;
        FilterHolder filterHolder = new FilterHolder();
        filterFields.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().length() >= 3).forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            switch (key) {
                case "inbound":
                    BooleanExpression isInbound = Boolean.valueOf(filterFields.get(key)) ? message.inbound.isTrue() : message.inbound.isFalse();
                    filterHolder.and(isInbound);
                    break;
                case "terminal":
                    if (Boolean.valueOf(filterFields.get(key))) {
                        message.attempts.any().events.any().terminal.isTrue();
                    } else {
                        message.attempts.any().events.any().terminal.isFalse();
                    }
                    break;
                case "status":
                    filterHolder.and(message.attempts.any().events.any().status.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "id":
                    filterHolder.and(message.id.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "sender":
                    filterHolder.and(message.sender.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "recipient":
                    filterHolder.and(message.recipient.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "document_type":
                        filterHolder.and(message.documentType.equalsIgnoreCase(filterFields.get(key)));
                    break;
                case "document_number":
                    filterHolder.and(message.documentNumber.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "document_date":
                    filterHolder.and(message.documentDate.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "due_date":
                    filterHolder.and(message.dueDate.containsIgnoreCase(filterFields.get(key)));
                    break;
                case "created":
                    //filterHolder.and(message.created.containsIgnoreCase(filterFields.get(key)));
                    //TODO: Add custom converter here or on upper level?
                    break;
            }
        });
        return filterHolder;
    }

    public int countByFilter(Map<String, String> filterFields) {
        FilterHolder filterHolder = fillFilterHolder(filterFields);
        return Math.toIntExact(messagesRepository.count(filterHolder.getFilter()));
    }

    private class FilterHolder {
        BooleanExpression filter;

        public FilterHolder() {
        }

        public FilterHolder(BooleanExpression filter) {
            this.filter = filter;
        }

        public FilterHolder and(BooleanExpression another) {
            if (filter == null) {
                filter = another;
            } else {
                filter = filter.and(another);
            }
            return this;
        }

        public FilterHolder or(BooleanExpression another) {
            if (filter == null) {
                filter = another;
            } else {
                filter = filter.or(another);
            }
            return this;
        }

        public BooleanExpression getFilter() {
            return filter;
        }
    }
}
