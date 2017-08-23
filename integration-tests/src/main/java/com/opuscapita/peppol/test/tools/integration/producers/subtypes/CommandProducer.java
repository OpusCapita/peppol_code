package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommandProducer implements Producer {
    private final static Logger logger = LoggerFactory.getLogger(CommandProducer.class);
    private final String command;

    public CommandProducer(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        logger.info("CommandProducer: starting, command to execute:" + command);
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            logger.info("CommandProducer: exit value: " + p.exitValue());
            p.destroy();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
