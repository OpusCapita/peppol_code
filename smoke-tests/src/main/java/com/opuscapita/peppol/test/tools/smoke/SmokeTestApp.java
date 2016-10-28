package com.opuscapita.peppol.test.tools.smoke;

import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
import com.opuscapita.peppol.test.tools.smoke.configs.SmokeTestConfig;
import com.opuscapita.peppol.test.tools.smoke.configs.util.SmokeTestConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by bambr on 16.20.10.
 */
public class SmokeTestApp {

    private final static Logger logger = LogManager.getLogger(SmokeTestApp.class);

    public static void main(String[] args) {

        logger.info("Starting:  " + SmokeTestApp.class);
        String configFile = args[0];
        if(configFile == null || configFile.isEmpty()){
            logger.error("Config file not specified, exiting!");
            return;
        }

        SmokeTestConfig config = new SmokeTestConfigReader().initConfig(configFile);
        List<CheckResult> checkResults = config.runChecks();
        printResult(checkResults);
    }

    private static void printResult(List<CheckResult> checkResults) {
        for(CheckResult result : checkResults){
            logger.info(result.toString());
        }
    }
}
