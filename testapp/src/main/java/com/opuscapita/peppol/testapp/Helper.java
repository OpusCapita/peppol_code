package com.opuscapita.peppol.testapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("unused")
@Component
public class Helper {
    @SuppressWarnings("WeakerAccess")
    public static final String NAME = "helper";

    private final ApplicationContext context;

    @Autowired
    public Helper(ApplicationContext context) {
        this.context = context;
    }

}
