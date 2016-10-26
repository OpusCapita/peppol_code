package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class MqConnectionCheck extends Check {

    public MqConnectionCheck(String moduleName, Map<String, String> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        return null;
    }
}
