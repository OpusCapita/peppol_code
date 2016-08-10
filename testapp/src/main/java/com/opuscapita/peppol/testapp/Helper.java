package com.opuscapita.peppol.testapp;

import com.opuscapita.peppol.commons.container.ContainerMessageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("unused")
@Component
public class Helper {
    @SuppressWarnings("WeakerAccess")
    public static final String NAME = "helper";

    private final ContainerMessageFactory containerMessageFactory;

    @Autowired
    public Helper(ContainerMessageFactory containerMessageFactory) {
        this.containerMessageFactory = containerMessageFactory;
    }

    public ContainerMessageFactory getContainerMessageFactory() {
        return containerMessageFactory;
    }
}
