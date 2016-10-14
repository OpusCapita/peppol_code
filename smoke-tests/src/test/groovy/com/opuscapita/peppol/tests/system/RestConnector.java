package com.opuscapita.peppol.tests.system;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by BOGDAAN1 on 2016.09.05..
 */
public class RestConnector {

    //use JerseyRest

    @JsonProperty("base_url")
    private String baseURL;

    //api/events/getdocumentlistfromfile/id
    //api/events/getflowviewfiledetails/id


    public String getBaseURL() {
        return baseURL;
    }

    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

}
