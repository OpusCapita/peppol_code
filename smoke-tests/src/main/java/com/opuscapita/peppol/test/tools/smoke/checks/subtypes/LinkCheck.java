package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by bambr on 16.20.10.
 */
public class LinkCheck extends Check {

    public LinkCheck(String moduleName, Map<String, String> params) {
        super(moduleName,params);
    }

    @Override
    public CheckResult run() {
        CheckResult result;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(rawConfig.get("reference"));
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            assertEquals(HttpURLConnection.HTTP_OK, conn.getResponseCode());
            result = new CheckResult(name, true, "the link is reachable, HTTP Status code: " + conn.getResponseCode(), rawConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
            result = new CheckResult(name, false, "the link not reachable, " + ex, rawConfig);
        }
        return result;
    }
}
