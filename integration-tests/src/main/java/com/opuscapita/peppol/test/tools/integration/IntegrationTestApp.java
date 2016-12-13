package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestConfigReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.util.List;


/**
 * Created by gamanse1 on 2016.11.14..
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.opuscapita.peppol.test.tools.integration.model")
@ComponentScan(basePackages = {"com.opuscapita.peppol.test.tools.integration.integration", "com.opuscapita.peppol.test.tools.integration.util", /*"com.opuscapita.peppol.test.tools.integration.model"*/})
@EntityScan(basePackages = "com.opuscapita.peppol.commons.model")
public class IntegrationTestApp {
    private final static Logger logger = LogManager.getLogger(IntegrationTestApp.class);
    static String configFile;

    public static void main(String[] args) {
        logger.info("IntegrationTestApp : Starting!");
        SpringApplication.run(IntegrationTestApp.class);

        if (args.length < 1 || args[0] == null || args[0].isEmpty()) {
            logger.error("Configuration file not specified, exiting!");
            System.exit(1);
        }
        configFile = args[0];

        if(new File(configFile).isDirectory())
            configFile = configFile + "\\configuration.yaml";

        IntegrationTestConfig config = new IntegrationTestConfigReader(configFile).initConfig();
        List<TestResult> testResults = config.runTests();

        logger.info("IntegrationTestApp : Ended!");
    }
}
