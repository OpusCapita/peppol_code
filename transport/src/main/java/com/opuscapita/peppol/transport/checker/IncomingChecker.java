package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.TransportType;
import com.opuscapita.peppol.commons.container.status.ProcessingStatus;
import com.opuscapita.peppol.commons.container.status.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.storage.Storage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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

    private final RabbitTemplate rabbitTemplate;
    private final Storage storage;
    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;

    @Value("${peppol.transport.file.age.seconds:120}")
    private int age;
    @Value("${peppol.transport.input.directory}")
    private String directory;
    @Value("${peppol.transport.input.recursive:false}")
    private boolean recursive;
    @Value("${peppol.transport.input.mask:*.*}")
    private String mask;
    @Value("${peppol.transport.queue.out.name:preprocessing}")
    private String queue;

    @Autowired
    public IncomingChecker(@NotNull RabbitTemplate rabbitTemplate, @NotNull Storage storage, @Nullable StatusReporter statusReporter,
                           @NotNull ErrorHandler errorHandler) {
        this.rabbitTemplate = rabbitTemplate;
        this.storage = storage;
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
    }

    @Scheduled(fixedRate = 60_000) // 1 minute
    public void check() {
        try {
            receive(new File(directory));
        } catch (Exception e) {
            errorHandler.reportToServiceNow("Failed to process input data: " + ExceptionUtils.getStackTrace(e), "n/a", e, "Failed to read input file");
        }
    }

    private void receive(File directory) throws IOException {
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

    private void send(File file) throws IOException {
        String fileName = storage.moveToTemporary(file);
        logger.info("File moved to " + fileName);

        ContainerMessage cm = new ContainerMessage("From " + file.getAbsolutePath(), fileName, Endpoint.GATEWAY)
                .setStatus(new ProcessingStatus(TransportType.OUT_IN, "received", fileName));

        rabbitTemplate.convertAndSend(queue, cm);
        logger.info("File " + cm.getFileName() + " processed and sent to " + queue + " queue");

        if (statusReporter != null) {
            statusReporter.report(cm);
        }
    }


}
