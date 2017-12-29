package com.opuscapita.peppol.test.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class ContainerMessageTestLoader {

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public static ContainerMessage createContainerMessageFromFile(DocumentLoader documentLoader, File file) throws Exception {
        ContainerMessage containerMessage = new ContainerMessage("test", file.getAbsolutePath(), Endpoint.TEST);
        containerMessage.setDocumentInfo(documentLoader.load(file, Endpoint.TEST));
        System.out.println("Document recognized as " + containerMessage.getDocumentInfo().getDocumentType());
        containerMessage.getProcessingInfo().setCurrentStatus(Endpoint.TEST, "unit testing");
        return containerMessage;
    }

}
