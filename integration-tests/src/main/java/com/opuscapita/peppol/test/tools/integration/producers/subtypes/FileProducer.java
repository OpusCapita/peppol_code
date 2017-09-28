package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class FileProducer implements Producer {
    private final static Logger logger = LoggerFactory.getLogger(FileProducer.class);
    private String sourceFolder;
    private String destinationFolder;
    private File destination;
    private File source;

    public FileProducer(Object sourceFolder, Object destinationFolder) {
        this.sourceFolder = (String) sourceFolder;
        this.destinationFolder = (String) destinationFolder;
    }

    @Override
    public void run() {
        try {
            source = new File(this.sourceFolder);
            destination = new File(this.destinationFolder);
            if (!source.exists()) {
                logger.warn("File producer: " + this.sourceFolder + " doesn't exist, exiting!");
                return;
            }
            //if source is directory - then it's dir to dir copy
            if (source.isDirectory() && !destination.exists()) {
                logger.warn("File producer: " + this.destinationFolder + " doesn't exist, creating");
                Files.createDirectories(destination.toPath());
            }
            if (source.isDirectory()) {
                File[] files = source.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        processFile(file);
                    }
                }
            } else {
                processFile(source);
            }
        } catch (Exception ex) {
            logger.error("Error running FileProducer!", ex);
        }
    }

    private void processFile(File file) throws IOException { //testing hack for snc stub
        File destinationFile;
        //check if destination is already specified as file or just directory
        if (destination.isDirectory()) {
            destinationFile = new File(destinationFolder + "/" + file.getName());
        }
        else {
            destinationFile = destination;
        }
        if (destinationFile.getName().contains("file_not_stored")) {
            return;
        }
        logger.info("FileProducer: moving " + file.getAbsolutePath() + " -> " + destinationFile);
        FileUtils.copyFile(file, destinationFile);
    }
}
