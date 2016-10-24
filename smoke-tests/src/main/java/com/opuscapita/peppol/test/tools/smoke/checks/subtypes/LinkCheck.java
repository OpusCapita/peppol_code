package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.Assert.*;

/**
 * Created by bambr on 16.20.10.
 */
public class LinkCheck extends Check {

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
        } catch (Exception e) {
            e.printStackTrace();
            result = new CheckResult(name, false, "the link not reachable, " + e, rawConfig);
        }
        return result;
    }
}
