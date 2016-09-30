package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
@Lazy
public class IncomingChecker {
    private static final Logger logger = LoggerFactory.getLogger(IncomingChecker.class);

    @Value("${transport.file.age.seconds?:120}")
    private int age;

    @Value("${transport.input.directory}")
    private String directory;

    @Value("${transport.input.recursive?:false}")
    private boolean recursive;

    @Value("${transport.input.mask?:.*}")
    private String mask;

    @Value("${transport.input.queue?:internal_routing}")
    private String queue;

    @Value("${transport.backup.directory}")
    private String backup;

    private final DocumentLoader documentLoader;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public IncomingChecker(@NotNull DocumentLoader documentLoader, @NotNull RabbitTemplate rabbitTemplate) {
        this.documentLoader = documentLoader;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRate = 60_000) // 1 minute
    public void receive() throws IOException {
        receive(new File(directory));
    }

    private void receive(File directory) throws IOException {
        logger.debug("Checking directory " + directory.getAbsolutePath());

        Date earlier = DateUtils.addSeconds(new Date(), age);
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

            if (file.getAbsolutePath().matches(mask)) {
                send(file);
            }
        }
    }

    private void send(File file) throws IOException {
        BaseDocument doc = documentLoader.load(file);

        ContainerMessage cm = new ContainerMessage(doc, file.getAbsolutePath(), Endpoint.GATEWAY);
        cm.getDocument().setFileName(backupFile(file, cm));
        FileUtils.forceDelete(file);

        rabbitTemplate.convertAndSend(queue, cm);
        logger.info("File " + cm.getDocument().getFileName() + " processed and sent to MQ");
    }

    private String backupFile(File file, ContainerMessage cm) throws IOException {
        String senderId = normalizeFilename(cm.getDocument().getSenderId());
        String recipientId = normalizeFilename(cm.getDocument().getRecipientId());

        File backupDirectory = new File(backup + File.separator + senderId + File.separator + recipientId);
        if (!backupDirectory.mkdirs()) {
            throw new IOException("Failed to create backup directory: " + backupDirectory.getAbsolutePath());
        }

        FileUtils.copyFileToDirectory(file, backupDirectory);
        String result = backupDirectory.getAbsolutePath() + File.separator + FilenameUtils.getName(file.getAbsolutePath());
        logger.info("Incoming file " + file.getAbsolutePath() + " stored as " + result);

        return result;
    }

    // stolen from Oxalis
    private static String normalizeFilename(String s) {
        return s.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

}
