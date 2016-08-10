package com.opuscapita.peppol.commons.container.route.conf;

import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
public class DestinationFilter {
    private String mask;
    private String destination;

//    public DestinationFilter(@NotNull String mask, @NotNull String destination) {
//        this.mask = mask;
//        this.destination = destination;
//    }
//
    @NotNull
    public String getMask() {
        return mask;
    }

    @NotNull
    public String getDestination() {
        return destination;
    }

    public boolean matches(@NotNull String id) {
        return id.matches(mask);
    }

    public void setMask(@NotNull String mask) {
        this.mask = mask;
    }

    public void setDestination(@NotNull String destination) {
        this.destination = destination;
    }
}
