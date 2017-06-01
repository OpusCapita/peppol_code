package com.opuscapita.peppol.test.tools.smoke.checks;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public abstract class Check {
    protected String moduleName;
    protected Map<String, Object> rawConfig;

    public Check(String moduleName, Map<String, Object> params) {
        this.moduleName = moduleName;
        this.rawConfig = params;
    }

    public abstract CheckResult run();

    public String getModuleName() {
        return moduleName;
    }
}
