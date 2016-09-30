package com.opuscapita.peppol.testapp;

import com.opuscapita.peppol.internal_routing.controller.RoutingController;
import org.jetbrains.annotations.NotNull;
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
    public Helper(@NotNull ApplicationContext context) {
        this.context = context;
    }

    @NotNull
    public ApplicationContext getContext() {
        return context;
    }

    public RoutingController getRoutingController() {
        return context.getBean(RoutingController.class);
    }

}
