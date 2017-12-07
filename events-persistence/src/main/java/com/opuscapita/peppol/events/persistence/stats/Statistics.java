package com.opuscapita.peppol.events.persistence.stats;

import com.google.common.collect.EvictingQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Statistics {
    private final static AtomicLong lastMessageTimeStamp = new AtomicLong(0);
    private final static AtomicLong lastFailedMessageTimeStamp = new AtomicLong(0);
    private final static AtomicLong lastSuccessfulMessageTimeStamp = new AtomicLong(0);
    private final static EvictingQueue<Long> aggregateTiming = EvictingQueue.create(100);

    public static void updateLastSuccessful(long start) {
        long current = System.currentTimeMillis();
        lastSuccessfulMessageTimeStamp.set(current);
        lastMessageTimeStamp.set(current);
        aggregateTiming.add(current - start);
    }

    public static void updateLastFailed(long start) {
        long current = System.currentTimeMillis();
        lastFailedMessageTimeStamp.set(current);
        lastMessageTimeStamp.set(current);
        aggregateTiming.add(current - start);
    }

    public static long getLastMessageTimeStamp() {
        return lastMessageTimeStamp.get();
    }

    public static long getLastFailedMessageTimeStamp() {
        return lastFailedMessageTimeStamp.get();
    }

    public static long getLastSuccessfulMessageTimeStamp() {
        return lastSuccessfulMessageTimeStamp.get();
    }

    public static List<Long> getAggregateTiming() {
        return new ArrayList<>(aggregateTiming);
    }


}
