package com.opuscapita.peppol.commons.template;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
@FunctionalInterface
public interface ContainerMessageConsumer {
    void consume(@NotNull ContainerMessage cm) throws Exception;
}
