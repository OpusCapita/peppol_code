package com.opuscapita.peppol.ui.portal.util;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value(value = "${peppol.portal.reprocess.outbound.dir:/tmp}")
    private String reprocessOutboundDir;

    @Value(value = "${peppol.portal.reprocess.inbound.dir:/tmp}")
    private String reprocessInboundDir;

    @Value(value = "${peppol.portal.archive.directory}")
    private String archiveDirectory;

    @Value(value = "${peppol.portal.archive.tmpDirectory:/tmp}")
    private String tmpDirectory;

    private Map<String, String> catalogue = new HashMap<>();

    @PostConstruct
    public void initArchiveCatalogue() {
        File directory = new File(archiveDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        try {
            File[] lists = directory.listFiles((dir, name) -> name.endsWith(".list"));
            if (lists == null || lists.length < 1) {
                return;
            }
            for (File f : lists) {
                List<String> fileNames = FileUtils.readLines(f, Charset.defaultCharset());
                String archiveName = FilenameUtils.removeExtension(f.getName()) + ".tar.gz";
                fileNames.forEach(fn -> catalogue.put(fn, archiveName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Initialised archive catalogue with total of : " + catalogue.size() + " entries!");
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(catalogue);
            oos.close();
            logger.info("Archive size: " + baos.size() + " bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //new event about file being reprocessed should be handled in transports
    public void reprocess(Attempt attempt) {
        try {
            File fileToReprocess = new File(attempt.getFilename());
            File result = new File(attempt.getMessage().isInbound() ? reprocessInboundDir : reprocessOutboundDir, fileToReprocess.getName());
            IOUtils.copy(new FileInputStream(fileToReprocess), new FileOutputStream(result));
            logger.info("Reprocessing, file moved from: " + fileToReprocess.getAbsolutePath() + " to: " + result.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean fileArchived(String fileName) {
        return catalogue.containsKey(fileName);
    }

    public File extractFromArchive(String fileName) {
        if (!fileArchived(fileName)) {                                                                                                                  //not found in catalogue
            return null;
        }
        String targetFileName = tmpDirectory + new File(fileName).getName();
        String archive = archiveDirectory + catalogue.get(fileName);
        logger.info("Starting to unarchive file: " + fileName + " from: " + archive);
        try (                                                                                                                                           //using AutoCloseable
                                                                                                                                                        InputStream is = new BufferedInputStream(new GZIPInputStream(new FileInputStream(archive)));    //gz
                                                                                                                                                        TarArchiveInputStream tarInput = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);     //tar
                                                                                                                                                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFileName))                                       //target
        ) {
            TarArchiveEntry entry;
            while ((entry = tarInput.getNextTarEntry()) != null) {
                if (("/" + entry.getName()).equals(fileName)) {
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

        return null;   //should not happen
    }
}
