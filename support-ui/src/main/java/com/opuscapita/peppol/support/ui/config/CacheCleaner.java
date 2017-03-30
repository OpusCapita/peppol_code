package com.opuscapita.peppol.support.ui.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gamanse1 on 2017.03.28..
 */

public class CacheCleaner {

    @Scheduled(fixedRate = 30 * 1000) //every 30 seconds
    @CacheEvict(value = {"invalidMessages", "failedMessages", "sentMessages", "allOutboundMessages", "reprocessedMessages",
    "processingMessages", "invalidInboundMessages", "allInboundMessages", "allMessages"}, allEntries = true)
    public void cacheCleanUp() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        //System.out.println("Cache cleaned: " + timeStamp);
    }
}
