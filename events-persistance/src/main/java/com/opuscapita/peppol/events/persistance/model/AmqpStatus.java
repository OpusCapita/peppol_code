package com.opuscapita.peppol.events.persistance.model;

/**
 * Created by KACENAR1 on 14.24.2.
 */
public class AmqpStatus {
    private String queueName;
    private int size;
    private int consumers;

    public AmqpStatus() {
    }

    public AmqpStatus(int size, String queueName, int consumers) {
        this.size = size;
        this.queueName = queueName;
        this.consumers = consumers;
    }

    public int getConsumers() {
        return consumers;
    }

    public void setConsumers(int consumers) {
        this.consumers = consumers;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
