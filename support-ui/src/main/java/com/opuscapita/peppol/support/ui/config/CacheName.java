package com.opuscapita.peppol.support.ui.config;

import java.util.stream.Stream;

/**
 * Created by gamanse1 on 2017.03.22..
 */
public enum CacheName {
    allMessages("allMessages"),
    invalidMessages("invalidMessages"),
    failedMessages("failedMessages"),
    sentMessages("sentMessages"),
    allOutboundMessages("allOutboundMessages"),
    reprocessedMessages("reprocessedMessages"),
    processingMessages("processingMessages"),
    invalidInboundMessages("invalidInboundMessages"),
    allInboundMessages("allInboundMessages");

    private final String value;
    private CacheName(final String value) {
        this.value = value;
    }

    public String value(){
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static String[] getNames() {
        return Stream.of(CacheName.values()).map(CacheName::name).toArray(String[]::new);
    }
}
