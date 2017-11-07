package com.opuscapita.peppol.test.tools.integration.consumers;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public abstract class Consumer {
    private String id;
    protected String name;

    public Consumer(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract TestResult consume(Object consumable);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Consumer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
