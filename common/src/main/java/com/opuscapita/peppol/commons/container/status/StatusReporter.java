package com.opuscapita.peppol.commons.container.status;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class StatusReporter {
    private final RabbitTemplate rabbitTemplate;

    @Value("peppol.eventing.queue.in")
    private String reportDestination;

    @Autowired
    public StatusReporter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void report(@NotNull ContainerMessage cm) {
        rabbitTemplate.convertAndSend(reportDestination, cm);
    }

    public void reportError(@NotNull ContainerMessage cm, @Nullable Exception e, @NotNull String error) {
        if (!(cm.getBaseDocument() instanceof InvalidDocument)) {
            cm.setBaseDocument(new InvalidDocument(cm.getBaseDocument(), error, e));
        }

        rabbitTemplate.convertAndSend(reportDestination, cm.setStatus(error));
    }

}
