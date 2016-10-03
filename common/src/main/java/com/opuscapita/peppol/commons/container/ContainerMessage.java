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
@SuppressWarnings("WeakerAccess")
public class ContainerMessage implements Serializable {
    private final BaseDocument document;
    private final String metadata;
    private final Endpoint source;
    private Route route;

    public ContainerMessage(@NotNull BaseDocument document, @Nullable String metadata, @NotNull Endpoint source) {
        this.document = document;
        this.metadata = metadata;
        this.source = source;
    }

    public Route getRoute() {
        return route;
    }

    @NotNull
    public ContainerMessage setRoute(@NotNull Route route) {
        this.route = route;
        return this;
    }

    public BaseDocument getBaseDocument() {
        return document;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    /**
     * Returns customer ID depending on the direction of the message, either sender or recipient ID.
     */
    public String getCustomerId() {
        return isInbound() ? getBaseDocument().getRecipientId() : getBaseDocument().getSenderId();
    }

    @Override
    public String toString() {
        return document.getClass().getName() + " [" + route + "]";
    }

    @Nullable
    public String getPeppolMessageMetadata() {
        return metadata;
    }

    public Endpoint getSource() {
        return source;
    }
}
