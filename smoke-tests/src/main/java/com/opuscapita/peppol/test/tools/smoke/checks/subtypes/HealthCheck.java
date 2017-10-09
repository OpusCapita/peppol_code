package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class HealthCheck extends Check {

    private final static int DELAY = 8000;

    public HealthCheck(String moduleName, Map<String, Object> params) {
        super(moduleName,params);
    }

    @Override
    public CheckResult run() {
            for (int i = 0; i < 15; i++) {
                try {
                    return performCheck();      //doing check
                } catch (ConnectException e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (UnknownHostException hex){
                    return new CheckResult(moduleName, false, "Health check for: " +
                            rawConfig.get("reference") + " failed " + hex, rawConfig);
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        return new CheckResult(moduleName, false, "Health check for: " +
                rawConfig.get("reference") + " failed , module not accessible", rawConfig);
    }

    public CheckResult performCheck() throws Exception {
        URL url = new URL((String) rawConfig.get("reference"));
        URLConnection urlConn = url.openConnection();
        InputStreamReader is = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());

        JsonObject jsonObj = new Gson().fromJson(is, JsonObject.class);
        String statusValue = jsonObj.get("status").toString();
        statusValue = statusValue.replaceAll("\"", "");
        boolean statusCheck = statusValue.toUpperCase().equals("UP");

        return new CheckResult(moduleName, statusCheck, "Health check performed for: " +
                rawConfig.get("reference") + " received status is: " +
                statusValue, rawConfig);
    }
}
