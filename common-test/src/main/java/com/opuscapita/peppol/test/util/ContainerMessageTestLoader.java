package com.opuscapita.peppol.test.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ContainerMessageTestLoader {
    @NotNull
    public static ContainerMessage createContainerMessageFromFile(DocumentLoader documentLoader, File file) throws Exception {
        ContainerMessage containerMessage = new ContainerMessage("test", file.getAbsolutePath(), Endpoint.TEST);
        containerMessage.setDocumentInfo(documentLoader.load(file, Endpoint.TEST));
        containerMessage.getProcessingInfo().setCurrentStatus(Endpoint.TEST, "unit testing");
        return containerMessage;
    }
}
