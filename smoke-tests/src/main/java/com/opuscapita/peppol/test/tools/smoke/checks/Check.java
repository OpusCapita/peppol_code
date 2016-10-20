package com.opuscapita.peppol.test.tools.smoke.checks;

import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public interface Check {
    boolean init(Map<String, Object> rawConfig);

    CheckResult run();
}
