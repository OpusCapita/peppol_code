package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

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
            //selenide here
            for (File file : directory.listFiles()) {
                open(link);
                $("#datafile").uploadFile(file);
                $("#submit").click();
                String testResult = $("#validationStatus").getText();
                results.put(file.getName(), testResult.replaceAll("Validation status: ", ""));
            }
            saveResult(results);
        } catch (Exception ex) {
            logger.error("Error running web ui producer: ", ex);
            return;
        }
    }

    //Storing producer result
    private void saveResult(Properties results) throws Exception {
        results.store(new FileOutputStream(resultDirectory + "\\webUiResult"), null);
    }
}
