package com.opuscapita.peppol.ui.portal.model;

import com.opuscapita.peppol.commons.revised_model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessagesRepository extends PagingAndSortingRepository<Message, String> {
    public List<Message> findMessagesByInboundFalse();

    public Page<Message> findMessagesByInboundFalse(Pageable pageable);

    public List<Message> findMessagesByInboundTrue();

    public Page<Message> findMessagesByInboundTrue(Pageable pageable);

    public List<Message> findMessagesByInbound(boolean inbound);

    public Page<Message> findMessagesByInbound(boolean inbound, Pageable pageable);

    public Long countMessagesByInbound(boolean inbound);

    public Long countMessagesByInboundAndCreatedBetween(boolean inbound, long start, long end);
}
