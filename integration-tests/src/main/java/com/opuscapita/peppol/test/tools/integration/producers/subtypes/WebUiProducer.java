package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.log4j.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by gamanse1 on 2016.11.29..
 * This Producer posts test files to validation module's web ui using selenide library then stores
 * result to specified folder, this file supposed to be checked by WebUiConsumer after.
 */

public class WebUiProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(WebUiProducer.class);
    private String sourceDirectory;
    private String link;
    private String resultDirectory;

    public WebUiProducer(String sourceDirectory, String link, String resultDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.link = link;
        this.resultDirectory = resultDirectory;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void run() {
        Properties results = new Properties();
        File directory = null;
        try {
            directory = new File(sourceDirectory);
            if (!directory.isDirectory()) {
                logger.error(this.sourceDirectory + " doesn't exist!");
                return;
            }
        } catch (Exception ex) {
            logger.error("Error reading: " + sourceDirectory, ex);
            return;
        }
        try {
            HttpClient c = new HttpClient();
            for (File file : directory.listFiles()) {
                MultipartPostMethod mPost = new MultipartPostMethod(link);
                mPost.addParameter("datafile", file);
                mPost.addParameter("action", "upload");
                mPost.addParameter("ajax", "true");
                c.executeMethod(mPost);
                String response = mPost.getResponseBodyAsString();
                String testResult = response.contains("Validation status: true") ? "true" : "false";
                results.put(file.getName(), testResult);
            }
            saveResult(results);
        } catch (Throwable th) {
            logger.error("Error running web ui producer: ", th);
            return;
        }
    }

    private void saveResult(Properties results) throws Exception {
        results.store(new FileOutputStream(resultDirectory + "/webUiResult"), null);
    }
}
