package com.opuscapita.peppol.inbound;

import com.opuscapita.peppol.inbound.module.MessageHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {
        "com.opuscapita.peppol",
        "com.opuscapita.commons",
        "no.difi.oxalis.as2.inbound"})
public class InboundApp {
    private static MessageHandler mh;

    public InboundApp(@NotNull MessageHandler messageHandler) {
        mh = messageHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(InboundApp.class, args);
    }

    /**
     * A bit tricky thing, Oxalis uses Guice dependency injection while our code uses Spring.
     * This is the way how to inform Oxalis on what class to use.
     *
     * @return message handler bean managed by Spring
     */
    @NotNull
    public static MessageHandler getMessageHandler() {
        return mh;
    }

}
