package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.01..
 */
public interface ResultBuilder {
    void processResult(List<TestResult> checkResults);
}
