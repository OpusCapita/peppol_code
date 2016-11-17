package com.opuscapita.peppol.test.tools.integration.configs;

import com.opuscapita.peppol.test.tools.integration.util.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestConfig {
    List<Test> tests = new ArrayList<>();

    public void addTest(Test test) {
        tests.add(test);
    }
}
