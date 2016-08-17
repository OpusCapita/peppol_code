package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Holds the whole data exchange bean inside the application.
 *
 * @author Sergejs.Roze
 */
public class ContainerMessage implements Serializable {
    private final BaseDocument document;
    private final String origin;
    private final Endpoint source;
    private Route route;

    public ContainerMessage(@NotNull BaseDocument document, @Nullable String origin, @NotNull Endpoint source) {
        this.document = document;
        this.origin = origin;
        this.source = source;
    }

    public Route getRoute() {
        return route;
    }

    public BaseDocument getDocument() {
        return document;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    public String getCustomerId() {
        return isInbound() ? getDocument().getRecipientId() : getDocument().getSenderId();
    }

    @Override
    public String toString() {
        return document.getClass().getName() + " [" + route + "]";
    }

    @Nullable
    public String getOrigin() {
        return origin;
    }

    @NotNull
    public ContainerMessage setRoute(@NotNull Route route) {
        this.route = route;
        return this;
    }

    public Endpoint getSource() {
        return source;
    }
}
