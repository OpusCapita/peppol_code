package com.opuscapita.peppol.commons.container.xml.config;

public enum ConsistencyCheck {
    DISABLED("disabled"),
    ALL("all"),
    DOCUMENT_TYPE_IDENTIFIER("document_type_identifier");

    private final String value;

    ConsistencyCheck(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return this.value;
    }
}
