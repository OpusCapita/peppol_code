package com.opuscapita.peppol.transport.checker;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.DocumentInfo;
import com.opuscapita.peppol.commons.container.ProcessingInfo;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.storage.EmptyFileException;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import no.difi.vefa.peppol.common.model.Header;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Checks configured directory periodically and sends file to MQ.
 * Supports recursive walk through the directories.
 *
 * @author Sergejs.Roze
 */
@Component
public class IncomingChecker extends ValuesChecker {
    private static final Logger logger = LoggerFactory.getLogger(IncomingChecker.class);

    private final MessageQueue messageQueue;
    private final Storage storage;
    private final StatusReporter statusReporter;
    private final ErrorHandler errorHandler;
    private final DocumentLoader documentLoader;

    @Value("${peppol.component.name}")
    private String componentName;
    @Value("${peppol.file-to-mq.file.age.seconds:120}")
    private int age;
    @FileMustExist
    @Value("${peppol.file-to-mq.input.directory}")
    private String directory;
    @Value("${peppol.file-to-mq.input.recursive:false}")
    private boolean recursive;
    @Value("${peppol.file-to-mq.input.mask:*.*}")
    private String mask;
    @Value("${peppol.file-to-mq.queue.out.name:preprocessing}")
    private String queue;
    @Value("${peppol.file-to-mq.copy.directory:}")
    private String copyDir;
    @Value("${peppol.file-to-mq.reprocess:false}")
    private boolean reprocess;
    @Value("${peppol.file-to-mq.direction:OUT}")
    private String direction;
    @Value("${peppol.mlr-reporter.queue.in.name:''}")
    private String mlrReporterQueue;

    @Autowired
    public IncomingChecker(@NotNull MessageQueue messageQueue, @NotNull Storage storage, @Nullable StatusReporter statusReporter,
                           @NotNull ErrorHandler errorHandler, @NotNull DocumentLoader documentLoader) {
        this.messageQueue = messageQueue;
        this.storage = storage;
        this.statusReporter = statusReporter;
        this.errorHandler = errorHandler;
        this.documentLoader = documentLoader;
    }

    // check directory once per minute for new input files
    @Scheduled(fixedRate = 60_000) // 1 minute
    public void check() {
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) {
            String msg = directory + " is not a valid directory, please check the configuration";
            logger.error(msg);
            errorHandler.reportWithoutContainerMessage(null, new IllegalArgumentException(msg), msg, "peppol-ap", null);
        }

        try {
            receive(dir);
        } catch (Exception e) {
            errorHandler.reportWithoutContainerMessage(null, e, "Failed to process input directory", "peppol-ap", null);
        }
    }

    // check subdirectories, file age and file mask
    private void receive(File directory) {
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
                try {
                    send(file);
                } catch (Exception e) {
                    String shortDescription = "Failed to process input file, reason: " + (e.getMessage() == null ? "unknown" : e.getMessage());
                    errorHandler.reportWithoutContainerMessage(null, e, shortDescription, null, file.getAbsolutePath());
                    if (e instanceof EmptyFileException) {
                        sendEmptyFileReport(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    private void sendEmptyFileReport(@NotNull String fileName) {
        if (StringUtils.isBlank(mlrReporterQueue)) {
            return;
        }
        Endpoint endpoint = new Endpoint(componentName, getProcessType());
        String metadata = "Empty file " + fileName + " received by " + componentName;

        ContainerMessage cm = new ContainerMessage(fileName, endpoint);

        ProcessingInfo pi = new ProcessingInfo(endpoint);
        pi.setProcessingException(metadata);
        cm.setProcessingInfo(pi);

        DocumentInfo di = new DocumentInfo();
        di.setIssueDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        di.setSenderId("unknown");
        di.setSenderName("unknown");
        di.setRecipientId("unknown");
        di.setRecipientName("unknown");
        di.setDocumentId("empty_file");
        cm.setDocumentInfo(di);

        try {
            messageQueue.convertAndSend(mlrReporterQueue, cm);
        } catch (Exception e) {
            logger.error("Failed to send message to MLR reporter: " + e.getMessage(), e);
            errorHandler.reportWithContainerMessage(cm, e, "Failed to report empty file: " + fileName);
        }
    }

    // send file info to MQ if found
    private void send(File file) throws Exception {
        String fileName = storage.moveToTemporary(file, copyDir);
        logger.info("File moved to: " + fileName);
        Endpoint source = new Endpoint(componentName, getProcessType());

        ContainerMessage cm = new ContainerMessage(fileName, source);
        cm.setStatus(source, "received");
        cm.setOriginalFileName(file.getAbsolutePath());
        setMetadataOfTheContainerMessage(cm);

        EventingMessageUtil.reportEvent(cm, "Picked up file by transport");
        messageQueue.convertAndSend(queue, cm);
        logger.info("File " + cm.toLog() + " processed and sent to " + queue + " queue");

        if (statusReporter != null) {
            statusReporter.report(cm);
        }
    }

    /**
     * Sets the metadata information of the container message
     * First tries to extract the header from the document
     *  - If SBDH exists, uses it
     *  - If SBDH doesn't exist, extract information from payload
     **/
    private void setMetadataOfTheContainerMessage(ContainerMessage cm) {
        Header header = documentLoader.parseHeader(cm.getFileName());
        if (header != null) {
            TransportIncomingFileMetadata transmissionResult = new TransportIncomingFileMetadata(header);
            cm.getProcessingInfo().setPeppolMessageMetadata(PeppolMessageMetadata.create(transmissionResult));
        } else {
            cm.getProcessingInfo().setPeppolMessageMetadata(new PeppolMessageMetadata());
        }

        if (cm.isInbound()) {
            cm.getProcessingInfo().getPeppolMessageMetadata().setSendingAccessPoint(null);
        } else {
            cm.getProcessingInfo().getPeppolMessageMetadata().setReceivingAccessPoint(null);
        }
    }

    private ProcessType getProcessType() {
        if (direction.toUpperCase().equals("OUT")) {
            if (reprocess) {
                return ProcessType.OUT_REPROCESS;
            }
            return ProcessType.OUT_FILE_TO_MQ;
        } else { //IN
            if (reprocess) {
                return ProcessType.IN_REPROCESS;
            }
            throw new UnsupportedOperationException(componentName + " is not supposed to handle inbound messages");
        }
    }
}
