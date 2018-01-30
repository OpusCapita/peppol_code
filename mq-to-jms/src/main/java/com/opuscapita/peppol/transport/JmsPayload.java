package com.opuscapita.peppol.transport;

import java.util.Map;

public class JmsPayload {
    String payload;
    Map<String, Object> metadata;

    public JmsPayload(String payload) {
        this.payload = payload;
    }

    public JmsPayload(String payload, Map<String, Object> metadata) {
        this.payload = payload;
        this.metadata = metadata;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "JmsPayload{" +
                "payload='" + payload + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}
