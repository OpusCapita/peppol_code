package com.opuscapita.peppol.testapp;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class ScriptExecutor {

    private final Helper helper;
    private final Logger logger = LoggerFactory.getLogger(ScriptExecutor.class);

    @Autowired
    public ScriptExecutor(Helper helper) {
        this.helper = helper;
    }

    public void execute(Scenario scenario) {
        if (scenario.getScript() == null) {
            return;
        }

        logger.info("Executing scenario " + scenario.getName());
        Binding binding = new Binding();
        binding.setProperty(Helper.NAME, helper);

        GroovyShell shell = new GroovyShell(binding);
        Object result = shell.evaluate(scenario.getScript());

        logger.info("Execution result: " + result);
    }
}
