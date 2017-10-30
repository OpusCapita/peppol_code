package com.opuscapita.peppol.test.tools.validatorTester;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File directory = new File(args[0]);
        String link = args[1];
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
                String responseString = new BasicResponseHandler().handleResponse(response);
                ValidationResult result = new Gson().fromJson(responseString, ValidationResult.class);
                System.out.println(result);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void handleResponse(HttpResponse response) {

    }
}
