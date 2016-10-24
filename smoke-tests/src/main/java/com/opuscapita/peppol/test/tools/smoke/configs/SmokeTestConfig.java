package com.opuscapita.peppol.test.tools.smoke.configs;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bambr on 16.20.10.
 */
public class SmokeTestConfig {
    ArrayList<Check> checks = new ArrayList<>();

    public void AddCheck(Check check){
        checks.add(check);
    }

    public List<CheckResult> runChecks() {
        ArrayList<CheckResult> checkResults = new ArrayList<>();
        for(Check check : checks){
            checkResults.add(check.run());
        }
        return  checkResults;
    }
}
