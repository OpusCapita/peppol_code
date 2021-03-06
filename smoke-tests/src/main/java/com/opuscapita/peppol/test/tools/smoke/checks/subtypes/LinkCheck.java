package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class LinkCheck extends Check {

    public LinkCheck(String moduleName, Map<String, Object> params) {
        super(moduleName,params);
    }

    @Override
    public CheckResult run() {
        CheckResult result;
        HttpURLConnection conn = null;
        try {
            URL url = new URL((String)rawConfig.get("reference"));
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            boolean goodResponse = conn.getResponseCode() == HttpURLConnection.HTTP_OK;
            result = new CheckResult(moduleName, goodResponse, "HTTP Status code: " + conn.getResponseCode(), rawConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            result = new CheckResult(moduleName, false, rawConfig.get("reference") + " link is not reachable, " + ex, rawConfig);
        }
        return result;
    }
}
