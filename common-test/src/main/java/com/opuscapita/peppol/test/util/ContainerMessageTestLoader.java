package com.opuscapita.peppol.test.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ContainerMessageTestLoader {
    @Nullable
    public static ContainerMessage createContainerMessageFromFile(DocumentLoader documentLoader, File file) throws Exception {
        Endpoint endpoint = new Endpoint("test", ProcessType.TEST);
        ContainerMessage containerMessage = new ContainerMessage("test", file.getAbsolutePath(), endpoint);
        containerMessage.setDocumentInfo(documentLoader.load(file, endpoint));
        containerMessage.getProcessingInfo().setCurrentStatus(endpoint, "unit testing");
        return containerMessage;
    }
}
