package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Created by gamanse1 on 2016.11.29..
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

        Map<String, String> results = new HashMap<>();
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
        //selenide here
        open(link);
        for (File file : directory.listFiles()) {
            $("#datafile").uploadFile(file);
            $("#submit").click();
            String result = $("#validationStatus").getText();
            results.put(file.getName(), result.replaceAll("Validation status: ", ""));
        }
    }
}
