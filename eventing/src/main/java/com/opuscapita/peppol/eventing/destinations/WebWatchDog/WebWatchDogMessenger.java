package com.opuscapita.peppol.eventing.destinations.WebWatchDog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Daniil on 05.07.2016.
 */
public class WebWatchDogMessenger {
    WebWatchDogConfig webWatchDogConfig;

    public WebWatchDogMessenger(WebWatchDogConfig webWatchDogConfig) {
        this.webWatchDogConfig = webWatchDogConfig;
    }

    public void sendOk(String fileName) throws IOException {
        writeStatusFile(fileName, WebWatchDogStatus.OK);
    }

    public void sendFailed(String fileName) throws IOException {
        writeStatusFile(fileName, WebWatchDogStatus.FAILED);
    }

    public void sendInvalid(String fileName) throws IOException {
        writeStatusFile(fileName, WebWatchDogStatus.INVALID);
    }

    private void writeStatusFile(String fileName, String status) throws IOException {
        String fileNamePrefix = webWatchDogConfig.getPrefix();
        String loggerId = Arrays.asList(fileName.split("\\.")).get(0);
        String statusFileName = fileNamePrefix + loggerId;
        String content = loggerId+";"+status+";"+ System.currentTimeMillis();
        File statusFile = new File(webWatchDogConfig.getFolder(), statusFileName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(statusFile));
        bufferedWriter.write(content);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public boolean isApplicableForFile(String fileName) {
        return fileName.toLowerCase().startsWith("logger_") && fileName.toLowerCase().endsWith("document_types");
    }
}
