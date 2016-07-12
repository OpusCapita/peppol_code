package com.opuscapita.peppol.commons.container;

import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import com.opuscapita.peppol.commons.container.route.Status;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * Holds the whole data exchange bean inside the application.
 *
 * @author Sergejs.Roze
 */
public class ContainerMessage implements Serializable {
    private Route route;
    private BaseDocument document;
    private Endpoint source;
    private Status currentStatus;


    @NotNull
    public Route getRoute() {
        return route;
    }

    public void setRoute(@NotNull Route route) {
        this.route = route;
    }

//    @Nullable
//    public String getSenderId() {
//
//    }
//
//    @Nullable
//    public String getRecipientId() {
//
//    }

}
