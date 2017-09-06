package com.opuscapita.peppol.ui.portal.ui.views.fragments;

public enum GridFragmentType {
    INBOUND("Inbound"),
    OUTBOUND("Outbound"),
    SENDERS("Senders"),
    ACCESS_POINTS("Access points");
    private final String value;

    GridFragmentType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
