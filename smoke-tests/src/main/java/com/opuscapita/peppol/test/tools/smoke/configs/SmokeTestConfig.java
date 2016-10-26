package com.opuscapita.peppol.test.tools.smoke.configs;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by bambr on 16.20.10.
 */
public class SmokeTestConfig {
    ArrayList<Check> checks = new ArrayList<>();

    public void AddCheck(Check check){
        checks.add(check);
    }

    public List<CheckResult> runChecks() {
       return checks.stream().map(Check::run).collect(Collectors.toCollection(ArrayList::new));
    }
}
