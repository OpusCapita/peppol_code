package com.opuscapita.peppol.test.tools.integration;

import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.commons.servicenow.SncEntity;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.test.tools.integration.configs.IntegrationTestConfig;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import com.opuscapita.peppol.test.tools.integration.util.IntegrationTestConfigReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by gamanse1 on 2016.11.14..
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.opuscapita.peppol.commons")
public class IntegrationTestApp {
    private final static Logger logger = LogManager.getLogger(IntegrationTestApp.class);
    static String configFile;
    static String testResultFileName;
    static String templateDir;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        logger.info("IntegrationTestApp : Starting!");
        SpringApplication.run(IntegrationTestApp.class);

        if (args.length < 3 || args[0] == null || args[0].isEmpty()) {
            logger.error("Not all command line arguments specified!");
            logger.error("Required arguments are: configFile, testResultFileName, templateDir");
            System.exit(1);
        }
        configFile = args[0];
        testResultFileName = args[1];
        templateDir = args[2];

        if (new File(configFile).isDirectory())
            configFile = configFile + "\\configuration.yaml";

        IntegrationTestConfig config = new IntegrationTestConfigReader(configFile).initConfig();
        List<TestResult> testResults = config.runTests();
        //new HtmlResultBuilder(testResultFileName, templateDir).processResult(testResults);
        logger.info("IntegrationTestApp : Ended!");
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return new ServiceNow() {
            @Override
            public void insert(SncEntity sncEntity) throws IOException {
                System.out.println("Inserted incident: " + sncEntity);
            }
        };
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler(serviceNowRest(), environment);
    }
}
