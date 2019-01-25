package com.opuscapita.peppol.ui.portal.util;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.revised_model.Attempt;
import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final DocumentLoader documentLoader;
    private final MessageQueue messageQueue;

    @Value("${peppol.component.name}")
    private String componentName;

    @Value(value = "${peppol.portal.archive.directory}")
    private String archiveDirectory;

    @Value(value = "${peppol.portal.archive.tmpDirectory:/tmp}")
    private String tmpDirectory;

    @Value(value = "${peppol.portal.reprocess.queue.name:preprocessing}")
    private String queue;

    private File[] archiveLists;

    @Autowired
    public FileService(@NotNull MessageQueue messageQueue, @NotNull DocumentLoader documentLoader) {
        this.messageQueue = messageQueue;
        this.documentLoader = documentLoader;
    }

    @PostConstruct
    public void initArchiveCatalogue() {
        File directory = new File(archiveDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        archiveLists = directory.listFiles((dir, name) -> name.endsWith(".list"));
    }

    //new event about file being reprocessed should be handled in transports
    //do not support reprocessing of archived files
    public void reprocess(Attempt attempt) {
        try {
            File fileToReprocess = new File(attempt.getFilename());
            Endpoint source = new Endpoint(componentName, getProcessType(attempt));
            ContainerMessage cm = new ContainerMessage(fileToReprocess.getName(), source);
            cm.setStatus(source, "reprocessing");
            cm.setOriginalFileName(fileToReprocess.getAbsolutePath());
            messageQueue.convertAndSend(queue, cm);
            logger.info("File " + cm.toLog() + " sent to " + queue + " queue");
            /*File result = new File(attempt.getMessage().isInbound() ? reprocessInboundDir : reprocessOutboundDir, fileToReprocess.getName());
            IOUtils.copy(new FileInputStream(fileToReprocess), new FileOutputStream(result));
            logger.info("Reprocessing, file moved from: " + fileToReprocess.getAbsolutePath() + " to: " + result.getAbsolutePath());*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProcessType getProcessType(Attempt attempt) {
        return attempt.getMessage().isInbound() ? ProcessType.IN_REPROCESS : ProcessType.OUT_REPROCESS;
    }

    /***
     * @param attempt with a file name to search in archive
     * @return unarchived file or null if not found
     */
    public @Nullable
    File extractFromArchive(Attempt attempt) {
        for (File f : archiveLists) {
            List<String> fileNames = null;
            try {
                fileNames = FileUtils.readLines(f, Charset.defaultCharset());
                if (fileNames.contains(attempt.getFilename())) {
                    String archiveName = FilenameUtils.removeExtension(f.getName()) + ".tar.gz";
                    String targetFileName = tmpDirectory + new File(attempt.getFilename()).getName();
                    String archive = archiveDirectory + archiveName;
                    logger.info("Starting to unarchive file: " + attempt + " from: " + archive);
                    try (
                            InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(archive)));    //gz
                            TarArchiveInputStream tarInput = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);     //tar
                            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFileName))                                       //target
                    ) {
                        TarArchiveEntry entry;
                        while ((entry = tarInput.getNextTarEntry()) != null) {
                            if (("/" + entry.getName()).equals(attempt)) {
                                if (tarInput.canReadEntryData(entry)) {
                                    byte data[] = new byte[2048];
                                    int count;
                                    while ((count = tarInput.read(data)) != -1) {
                                        outputStream.write(data, 0, count);
                                    }
                                    outputStream.close();
                                    outputStream.flush();
                                    return new File(targetFileName);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
