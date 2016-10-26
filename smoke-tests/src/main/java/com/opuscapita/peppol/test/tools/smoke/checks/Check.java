package com.opuscapita.peppol.test.tools.smoke.checks;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public abstract class Check {
    protected String name;
    protected Map<String, String> rawConfig;

    public Check(String moduleName, Map<String, String> params) {
        this.name = moduleName;
        this.rawConfig = params;
    }

    public abstract CheckResult run();

    public String getName() {
        return name;
    }
}
