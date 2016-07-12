package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.NotNull;

/**
 * Single endpoint of the module/process/whatever in a whole route.
 * Currently it holds only one text value and doesn't require a separate class,
 * but introduced to be able to provide more settings.
 *
 * @author Sergejs.Roze
 */
public class Endpoint {
    private final String address;

    public Endpoint(@NotNull String address) {
        this.address = address;
    }

    @NotNull
    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "[" + address + "]";
    }
}
