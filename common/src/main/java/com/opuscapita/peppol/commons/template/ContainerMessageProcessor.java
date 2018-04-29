package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
public interface ContainerMessageProcessor {
    void process(@NotNull ContainerMessage cm);

    default void setContainerMessageConsumer(ContainerMessageConsumer controller) {}

    default void setReportEnabled(boolean enabled) {}
}
