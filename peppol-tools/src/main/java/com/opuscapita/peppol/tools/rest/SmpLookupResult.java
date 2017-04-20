package com.opuscapita.peppol.tools.rest;

/**
 * Created by bambr on 17.20.4.
 */
public class SmpLookupResult {
    String description;
    String url;

    public SmpLookupResult(String description, String url) {
        this.description = description;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "SmpLookupResult{" +
                "description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
