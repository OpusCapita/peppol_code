package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.storage.StorageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class TransportController {
    private static final Logger logger = LoggerFactory.getLogger(TransportController.class);

    @Value("${peppol.mq-to-file.output.directory}")
    private String directory;
    @Value("${peppol.mq-to-file.output.template:%FILENAME%}")
    private String template;
    @Value("${peppol.mq-to-file.backup.directory:''}")
    private String backupDir;

    public void storeMessage(@NotNull ContainerMessage cm) throws IOException {
        File input = new File(cm.getFileName());
        File output = new File(directory, TemplateTools.templateToPath(template, cm));

        copyFile(input, output);

        logger.info("File " + cm.getFileName() + " copied to " + output);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    protected void copyFile(@NotNull File input, @NotNull File output) throws IOException {
        File result = output;
        if (output.exists()) {
            result = StorageUtils.prepareUnique(new File(output.getParent()), output.getName());
            logger.warn("File " + output.getAbsolutePath() + " exists, storing file as " + result.getAbsolutePath());
        }

        new File(output.getParent()).mkdirs();
        FileUtils.copyFile(input, result);

        if (StringUtils.isNotBlank(backupDir)) {
            FileUtils.copyFileToDirectory(result, new File(backupDir));
            logger.info("Backup created as " + backupDir + File.separator + result.getName());
        }
    }

    @SuppressWarnings("SameParameterValue")
    void setDirectory(@NotNull String directory) {
        this.directory = directory;
    }

    @SuppressWarnings("SameParameterValue")
    void setTemplate(@NotNull String template) {
        this.template = template;
    }

}
