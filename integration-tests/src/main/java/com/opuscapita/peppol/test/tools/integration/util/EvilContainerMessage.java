package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;

public class EvilContainerMessage extends ContainerMessage {

    private static final long serialVersionUID = -1L;
    public EvilContainerMessage(String s, String absolutePath, Endpoint endpoint) {
        super(s,absolutePath,endpoint);
    }
}
