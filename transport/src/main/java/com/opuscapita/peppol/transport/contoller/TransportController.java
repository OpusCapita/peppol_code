package com.opuscapita.peppol.transport.contoller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class TransportController {
    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);

    @Value("${transport.output.directory}")
    private String directory;

    public void storeMessage(@NotNull ContainerMessage cm) throws IOException {
        File input = new File(cm.getBaseDocument().getFileName());

        FileUtils.copyFileToDirectory(input, new File(directory));
        logger.info("File " + cm.getBaseDocument().getFileName() + " stored to " + directory);
    }

}
