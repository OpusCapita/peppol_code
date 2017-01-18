package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.status.ProcessingStatus;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.storage.Storage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * Checks configured directory periodically and sends file to MQ.
 * Supports recursive walk through the directories.
 *
 * @author Sergejs.Roze
 */
@Component
public class IncomingChecker {
    private static final Logger logger = LoggerFactory.getLogger(IncomingChecker.class);

    private final MessageQueue messageQueue;
    private final Storage storage;
    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;

    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.file-to-mq.file.age.seconds:120}")
    private int age;
    @Value("${peppol.file-to-mq.input.directory}")
    private String directory;
    @Value("${peppol.file-to-mq.input.recursive:false}")
    private boolean recursive;
    @Value("${peppol.file-to-mq.input.mask:*.*}")
    private String mask;
    @Value("${peppol.file-to-mq.queue.out.name:preprocessing}")
    private String queue;
    @Value("${peppol.file-to-mq.backup.directory:}")
    private String backupDir;

    @Autowired
    public IncomingChecker(@NotNull MessageQueue messageQueue, @NotNull Storage storage, @Nullable StatusReporter statusReporter,
                           @NotNull ErrorHandler errorHandler) {
        this.messageQueue = messageQueue;
        this.storage = storage;
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
    }

    // check directory once per minute for new input files
    @Scheduled(fixedRate = 60_000) // 1 minute
    public void check() {
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            String msg = directory + " is not a valid directory, please check the configuration";
            logger.error(msg);
            errorHandler.reportToServiceNow("", "n/a", new IllegalArgumentException(msg), msg);
        }

        try {
            receive(dir);
        } catch (Exception e) {
            errorHandler.reportToServiceNow("", "n/a", e, "Failed to read input file: " + e.getMessage());
        }
    }

    // check subdirectories, file age and file mask
    private void receive(File directory) throws Exception {
        logger.debug("Checking directory " + directory.getAbsolutePath());

        Date earlier = DateUtils.addSeconds(new Date(), -age);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);

        Iterator<File> files = FileUtils.iterateFiles(directory, ageFileFilter, null);
        while (files.hasNext()) {
            File file = files.next();

            if (file.isDirectory()) {
                if (recursive) {
                    receive(file);
                }
                continue;
            }

            if (FilenameUtils.wildcardMatch(file.getName(), mask)) {
                logger.info("Found outgoing file: " + file.getAbsolutePath());
                send(file);
            }
        }
    }

    // send file info to MQ if found
    private void send(File file) throws Exception {
        String fileName = storage.moveToTemporary(file, backupDir);
        logger.info("File moved to: " + fileName);

        ContainerMessage cm = new ContainerMessage("Received by " + componentName + " as " + file.getAbsolutePath(),
                fileName, new Endpoint(componentName, Endpoint.Type.GATEWAY))
                    .setStatus(new ProcessingStatus(componentName, "received", fileName));

        messageQueue.convertAndSend(queue, cm);
        logger.info("File " + cm.getFileName() + " processed and sent to " + queue + " queue");

        if (statusReporter != null) {
            statusReporter.report(cm);
        }
    }

}
