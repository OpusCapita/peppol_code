package com.opuscapita.peppol.wwd.webwatchdog;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Daniil on 05.07.2016.
 */
@Component
public class WebWatchDogMessenger {
    private final static Logger logger = LoggerFactory.getLogger(WebWatchDogMessenger.class);

    WebWatchDogConfig webWatchDogConfig;

    @Autowired
    public WebWatchDogMessenger(WebWatchDogConfig webWatchDogConfig) {
        this.webWatchDogConfig = webWatchDogConfig;
    }

    public static boolean isApplicableForFile(String fileName) {
        fileName = ensureOnlyFileName(fileName);
        return fileName.toLowerCase().startsWith("logger_") && fileName.toLowerCase().endsWith("xml");
    }

    @NotNull
    protected static String ensureOnlyFileName(String fileName) {
        fileName = new File(fileName).getName();
        return fileName;
    }

    public void sendOk(String fileName) throws IOException {
        writeStatusFile(ensureOnlyFileName(fileName), WebWatchDogStatus.OK);
    }

    public void sendFailed(String fileName) throws IOException {
        writeStatusFile(ensureOnlyFileName(fileName), WebWatchDogStatus.FAILED);
    }

    public void sendInvalid(String fileName) throws IOException {
        writeStatusFile(ensureOnlyFileName(fileName), WebWatchDogStatus.INVALID);
    }

    private void writeStatusFile(String fileName, String status) throws IOException {
        String fileNamePrefix = webWatchDogConfig.getPrefix();
        String loggerId = Arrays.asList(fileName.split("\\.")).get(0);
        String statusFileName = fileNamePrefix + loggerId;
        String content = loggerId + ";" + status + ";" + System.currentTimeMillis();
        File statusFile = new File(webWatchDogConfig.getFolder(), statusFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(statusFile));
        bufferedWriter.write(content);
        bufferedWriter.flush();
        bufferedWriter.close();
        logger.info("Stored " + statusFile.getAbsolutePath());
    }
}
