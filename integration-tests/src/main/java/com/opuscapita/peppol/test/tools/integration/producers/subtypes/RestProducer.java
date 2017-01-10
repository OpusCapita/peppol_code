package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class RestProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(RestProducer.class);
    private final String sourceDirectory;
    private final String link;
    private final String method;
    private String restResultDirectory;
    private List<String> results = new ArrayList<>();

    public RestProducer(Object sourceDirectory, Object link, Object method, String restResultDirectory) {
        this.sourceDirectory = (String) sourceDirectory;
        this.link = (String) link;
        this.method = (String) method;
        this.restResultDirectory = restResultDirectory;
    }

    @Override
    public void run() {
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
        for (File file : directory.listFiles()) {
            try {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                HttpPost post = new HttpPost(link);
                HttpClient client = new DefaultHttpClient();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody fb = new FileBody(file);

                builder.addPart("file", fb);
                final HttpEntity entity = builder.build();

                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                handleResponse(response);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        saveResult();
    }

    private void saveResult() {
        logger.info("RestProducer: results count: " + results.size());
        if (results.isEmpty())
            return;
        restResultDirectory += "/restResult";
        try (FileWriter writer = new FileWriter(restResultDirectory)){
            logger.info("RestProducer: saving result to " + restResultDirectory);
            for (String str : results) {
                writer.write(str+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleResponse(HttpResponse response) {
        try {
            String responseString = new BasicResponseHandler().handleResponse(response);
            results.add(responseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
