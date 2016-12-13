package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

/**
 * Created by gamanse1 on 2016.12.09..
 */
public class RestConsumer extends Consumer {

    public RestConsumer(String id) {
        super(id);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void consume(String consumable) {
        String test = "{\"validationType\":\"UBL\",\"passed\":true,\"errors\":[]}";
        ValidationResult result = new Gson().fromJson(test,ValidationResult.class);
        String h = "l";
    }
}
