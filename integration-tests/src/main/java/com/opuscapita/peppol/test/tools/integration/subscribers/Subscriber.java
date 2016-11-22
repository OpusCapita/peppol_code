package com.opuscapita.peppol.test.tools.integration.subscribers;

import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public abstract class Subscriber {
    //timeout, ждёт результат выполнения модуля
    List<Consumer> consumers;
    private Object timeout;

    public Subscriber(Object timeout) {
        this.timeout = timeout;
    }
}
