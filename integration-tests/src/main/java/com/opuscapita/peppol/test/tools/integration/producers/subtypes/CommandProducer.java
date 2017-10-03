package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandProducer implements Producer {
    private final static Logger logger = LoggerFactory.getLogger(CommandProducer.class);
    private final String command;
    private final String dir;

    public CommandProducer(String command, String preprocessingCleanupDir) {
        this.command = command;
        this.dir = preprocessingCleanupDir;
    }

    @Override
    public void run() {
        logger.info("CommandProducer: starting, command to execute:" + command);
        logger.info("CommandProducer: cleaning directory" + dir);
        try {
            FileUtils.cleanDirectory(new File(dir));
        } catch (IOException e) {
            logger.warn("failed to cleanup directory: " + e);
        }
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null) {
                /*if(!s.contains("[org.apache.http.wire] http-outgoing-0")) {
                    logger.info("Script says: " + s);
                }*/
            }
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
