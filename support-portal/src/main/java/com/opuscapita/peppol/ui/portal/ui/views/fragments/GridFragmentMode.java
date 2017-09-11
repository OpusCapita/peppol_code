package com.opuscapita.peppol.ui.portal.ui.views.fragments;

public enum GridFragmentMode {
    ALL("All"),
    DELIVERED("Delivered"),
    FAILED("Failed"),
    REJECTED("Rejected"),
    REPROCESSING("Reprocessing");


    private final String value;

    GridFragmentMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
