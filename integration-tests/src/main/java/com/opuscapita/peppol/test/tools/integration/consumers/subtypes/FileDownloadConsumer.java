package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.opuscapita.peppol.test.tools.integration.IntegrationTestApp;
import com.opuscapita.peppol.test.tools.integration.consumers.Consumer;
import com.opuscapita.peppol.test.tools.integration.producers.subtypes.WebUiProducer;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;

import java.io.*;


/**
 * Created by gamanse1 on 2016.11.17..
 */
public class FileDownloadConsumer extends Consumer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(WebUiProducer.class);
    private final String name;
    private boolean expectedValue;
    private String link;
    private String action;

    public FileDownloadConsumer(String id, Object name, Object action, Object link, Object expectedValue) {
        super(id);
        this.name = (String)name;
        this.action = (String)action;
        this.link = (String)link;
        this.expectedValue = (boolean)expectedValue;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public TestResult consume(Object consumable) {
        logger.info("FileDownloadConsumer: starting!");
        TestResult testResult = null;
        CloseableHttpClient httpclient = HttpClients.custom().build();
        File consumableFile = new File((String)consumable);
        String fileToCheck = consumableFile.getName();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(IntegrationTestApp.tempDir + "download_test.xml");
            String userCredentials = "peppol_ap:peppol123AP";
            String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
            //"http://localhost:8080/rest/outbound/download/
            HttpGet httpget = new HttpGet(link + fileToCheck);
            httpget.setHeader("Authorization", basicAuth);
            httpget.setHeader("Content-Type", "text/html");
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                fileOutputStream.write(EntityUtils.toByteArray(response.getEntity()));
                fileOutputStream.close();
            } finally {
                response.close();
            }

            BufferedReader br = new BufferedReader(new FileReader(IntegrationTestApp.tempDir + "download_test.xml"));
            if ( (br.readLine() != null) != expectedValue)
                testResult = new TestResult(name, false, "File download test failed!" +
                " Expected value: "+ expectedValue);
            else
                testResult = new TestResult(name, true, "File download test successful!");
            br.close();
        } catch (Throwable th) {
            logger.error("Error running web ui producer: ", th);
          //  close();
            testResult = new TestResult(name, false, "Failed to execute FileDownloadConsumer: " + th);
        }
        finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return testResult;
    }
}
