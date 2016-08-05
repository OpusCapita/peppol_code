package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Route;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Holds the whole data exchange bean inside the application.
 *
 * @author Sergejs.Roze
 */
public class ContainerMessage implements Serializable {
    private final Route route;
    private final BaseDocument document;

    public ContainerMessage(@NotNull Route route, @NotNull BaseDocument document) {
        this.route = route;
        this.document = document;
    }

}
