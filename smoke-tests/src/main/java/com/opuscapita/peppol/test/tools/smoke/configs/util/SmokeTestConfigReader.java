package com.opuscapita.peppol.test.tools.smoke.configs.util;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.ChecksFactory;
import com.opuscapita.peppol.test.tools.smoke.configs.SmokeTestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class SmokeTestConfigReader {
    private final static Logger logger = LogManager.getLogger(SmokeTestConfigReader.class);

    public SmokeTestConfig initConfig(String configFile) {
        Yaml yaml = new Yaml();
        SmokeTestConfig testConfig = new SmokeTestConfig();
        try {
            System.out.println(yaml.dump(yaml.load(new FileInputStream(new File(configFile)))));
            Map<String, ArrayList> yamlParser  = (Map<String, ArrayList>) yaml
                    .load(new FileInputStream(new File(configFile)));

            Map<String, Object> rawChecks = (Map<String, Object>) yamlParser.get("tests");
            for (Map.Entry<String, Object> rawCheck : rawChecks.entrySet()) {
                String moduleName = rawCheck.getKey();

                Map<String,?> configuration = (Map<String,?>) rawCheck.getValue();
                String type = (String)configuration.get("type");

                Map<String, String> params = (Map)configuration.get("params");
                Check check = ChecksFactory.createCheck(moduleName,type,params);
                testConfig.AddCheck(check);
            }
        }
        catch (Exception ex) {
            logger.error("Error reading configuration file "+ configFile,ex);
        }
        return testConfig;
    }

}
