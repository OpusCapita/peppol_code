package com.opuscapita.peppol.test.tools.integration.consumers;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public abstract class Consumer {
    private String id;

    public Consumer(String id) {
        this.id = id;
    }

    public abstract boolean isDone();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract TestResult consume(String consumable);
}
