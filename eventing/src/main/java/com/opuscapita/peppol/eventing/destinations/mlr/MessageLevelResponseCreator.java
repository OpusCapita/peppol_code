package com.opuscapita.peppol.eventing.destinations.mlr;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Creates UBL 2.0 MLR response files.
 *
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class MessageLevelResponseCreator {

    /**
     * Creates MLR file about an error.
     *
     * @param cm the container message
     */
    public void reportError(@NotNull ContainerMessage cm) {

    }

    /**
     * Creates MLR file about a successfull end of processing.
     *
     * @param cm the container message
     */
    public void reportSuccess(@NotNull ContainerMessage cm) {

    }
}
