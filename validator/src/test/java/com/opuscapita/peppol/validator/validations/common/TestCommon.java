package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by bambr on 17.21.2.
 */
public class TestCommon {
    protected void runTestsOnDocumentProfile(String documentProfile, Consumer<? super File> consumer) {
        File resourceDir = new File(this.getClass().getResource("/test_data/" + documentProfile + "_files").getFile());
        String[] dataFiles = resourceDir.list((dir, name) -> name.toLowerCase().endsWith("xml"));
        Arrays.stream(dataFiles).map(fileName -> {
            System.out.println(resourceDir + File.separator + fileName);
            return new File(resourceDir, fileName);
        }).filter(fileToCheck -> fileToCheck.isFile() && fileToCheck.exists()).forEach(consumer);
    }

    @Nullable
    protected ContainerMessage createContainerMessageFromFile(DocumentLoader documentLoader, File file) throws Exception {
        Endpoint endpoint = new Endpoint("test", ProcessType.TEST);
        ContainerMessage containerMessage = new ContainerMessage("test", file.getAbsolutePath(), endpoint);
        containerMessage.setDocumentInfo(documentLoader.load(file, endpoint));
        containerMessage.getProcessingInfo().setCurrentStatus(endpoint, "unit testing");
        return containerMessage;
    }
}
