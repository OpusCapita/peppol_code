package com.opuscapita.peppol.test.tools.smoke;

import com.opuscapita.peppol.test.tools.smoke.checks.*;
import com.opuscapita.peppol.test.tools.smoke.configs.SmokeTestConfig;
import com.opuscapita.peppol.test.tools.smoke.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by bambr on 16.20.10.
 */
public class SmokeTestApp {

    private final static Logger logger = LogManager.getLogger(SmokeTestApp.class);
    static String configFile;
    static String testResultFileName;
    static String templateDir;

    public static void main(String[] args) {

        logger.info("SmokeTestApp : Starting!");
        configFile = args[0];
        testResultFileName = args[1];
        templateDir = args[2];

        if(configFile == null || configFile.isEmpty()){
            logger.error("Config file not specified, exiting!");
            return;
        }

        if(testResultFileName == null || testResultFileName.isEmpty()){
            logger.error("Test results file name empty or not specified, exiting!");
            return;
        }

        if(templateDir == null || templateDir.isEmpty()){
            logger.error("Template directory empty or not specified, exiting!");
            return;
        }

        SmokeTestConfig config = new SmokeTestConfigReader(configFile).initConfig();
        List<CheckResult> checkResults = config.runChecks();
        new LoggingResultBuilder().processResult(checkResults);
        new HtmlResultBuilder(testResultFileName, templateDir).processResult(checkResults);
        logger.info("SmokeTestApp: Finished!");
        int fails = (int)checkResults.stream().filter(res -> !res.isPassed()).count();
        System.exit(fails);
    }
}
