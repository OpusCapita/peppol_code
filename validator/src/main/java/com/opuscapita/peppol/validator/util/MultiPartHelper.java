package com.opuscapita.peppol.validator.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.storage.Storage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Created by bambr on 17.19.4.
 */
public class MultiPartHelper {
    @NotNull
    public static ContainerMessage createContainerMessageFromMultipartFile(DocumentLoader documentLoader, Endpoint endpoint, Storage storage, MultipartFile file, String origin, Logger logger) throws Exception {
        ContainerMessage containerMessage;
        String tempFilePath = storage.storeTemporary(file.getInputStream(), UUID.randomUUID().toString());
        logger.info("Validating file received via " + origin + " call and stored as " + tempFilePath);

        containerMessage = new ContainerMessage(origin + " /validate", tempFilePath, endpoint);
        containerMessage.setDocumentInfo(documentLoader.load(tempFilePath, endpoint));
        containerMessage.getProcessingInfo().setCurrentStatus(endpoint, origin + " validation");
        return containerMessage;
    }
}
