package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by bambr on 17.21.2.
 */
public class TestCommon {
    public void runTestsOnDocumentProfile(String documentProfile, Consumer<? super File> consumer) {
        File resourceDir = new File(this.getClass().getResource("/test_data/" + documentProfile + "_files").getFile());
        String[] dataFiles = resourceDir.list((dir, name) -> name.toLowerCase().endsWith("xml"));
        Arrays.stream(dataFiles).map(fileName -> {
            System.out.println(resourceDir + File.separator + fileName);
            return new File(resourceDir, fileName);
        }).filter(fileToCheck -> fileToCheck.isFile() && fileToCheck.exists()).forEach(consumer);
    }

    @Nullable
    public ContainerMessage createContainerMessageFromFile(DocumentLoader documentLoader, File file) throws IOException {
        ContainerMessage containerMessage = new ContainerMessage("test", file.getName(), new Endpoint("test", Endpoint.Type.PEPPOL))
                .setBaseDocument(documentLoader.load(file));
        if (containerMessage.getBaseDocument() instanceof InvalidDocument) {
            return null;
        }
        return containerMessage;
    }
}
