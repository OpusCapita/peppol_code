package com.opuscapita.peppol.test.tools.smoke.util;

import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.01..
 */
public interface ResultBuilder {
    void processResult(List<CheckResult> checkResults);
}
