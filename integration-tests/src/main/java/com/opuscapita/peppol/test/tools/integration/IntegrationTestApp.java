package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestConfigReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestApp {
    private final static Logger logger = LogManager.getLogger(IntegrationTestApp.class);
    static String configFile;

    public static void main(String[] args) {
        logger.info("IntegrationTestApp : Starting!");
        configFile = args[0];

        if(configFile == null || configFile.isEmpty()){
            logger.error("Configuration file not specified, exiting!");
            System.exit(1);
        }

        IntegrationTestConfig config = new IntegrationTestConfigReader(configFile).initConfig();
        List<TestResult> testResults = config.runTests();

        logger.info("IntegrationTestApp : Ended!");
    }
}
