package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.IntegrationTest;
import com.opuscapita.peppol.test.tools.integration.test.IntegrationTestFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class IntegrationTestConfigReader {

    private final static Logger logger = LogManager.getLogger(IntegrationTestConfigReader.class);
    private String configFile;
    private Map<String, Object> genericConfiguration = new HashMap<>();

    public IntegrationTestConfigReader(String configFile) {
        this.configFile = configFile;
    }

    public IntegrationTestConfig initConfig() {
        Yaml yaml = new Yaml();
        IntegrationTestConfig testConfig = new IntegrationTestConfig();
        try{
            Map<String, ArrayList> yamlParser  = (Map<String, ArrayList>) yaml
                    .load(new FileInputStream(new File(configFile)));
            Map<String, Object> configuration = (Map<String, Object>) yamlParser.get("configurations");
            Map<String, Object> modules = (Map<String, Object>) yamlParser.get("tests");
            loadConfiguration(configuration);

            for(Map.Entry<String,Object> module : modules.entrySet()){
                String moduleName = module.getKey();
                Map<String,?> moduleConfig = (Map<String, ?>) module.getValue();

                IntegrationTest test = IntegrationTestFactory.createTest(moduleName, moduleConfig, genericConfiguration);
                testConfig.addTest(test);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            logger.error("Error reading config file: "+configFile, ex);
        }
        return testConfig;
    }

    private void loadConfiguration(Map<String, Object> configuration) {
        Map<String, String> databases = (Map<String, String>) configuration.get("databases");
        Map<String, String> queues = (Map<String, String>) configuration.get("queues");
        genericConfiguration.putAll(databases);
        genericConfiguration.putAll(queues);
        genericConfiguration.put("validation result folder", configuration.get("validation result folder"));
    }
}
