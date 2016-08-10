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

    public Route getRoute() {
        return route;
    }

    public BaseDocument getDocument() {
        return document;
    }

    public boolean isInbound() {
        return route.isInbound();
    }

    public String getCustomerId() {
        return isInbound() ? getDocument().getRecipientId() : getDocument().getSenderId();
    }
    @Override
    public String toString() {
        return document.getClass().getName() + " [" + route + "]";
    }
}
