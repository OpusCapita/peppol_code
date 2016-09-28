package com.opuscapita.peppol.testapp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.opuscapita.commons.servicenow.ServiceNow;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point of the test application.
 *
 * @author Sergejs.Roze
 */
@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol", "com.opuscapita.commons"})
public class TestApp implements CommandLineRunner {
    private final static Options options = new Options();

    private final ScriptExecutor scriptExecutor;

    @Autowired
    public TestApp(ScriptExecutor scriptExecutor) {
        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void run(String... args) throws Exception {
        new JCommander(options, args);

        for (String script : options.scenarios) {
            try (Scenario current = new Scenario().load(script)) {
                scriptExecutor.execute(current);
            }
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }

    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "CanBeFinal"})
    private static class Options {
        @Parameter
        private List<String> scenarios = new ArrayList<>();
    }

    @Bean
    public ErrorHandler errorHandler() {
        return null;
    }

    @Bean
    public ServiceNow serviceNowRest() {
        return sncEntity -> {};
    }
}
