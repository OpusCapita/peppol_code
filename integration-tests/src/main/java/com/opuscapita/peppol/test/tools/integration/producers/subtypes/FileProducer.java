package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
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

    public FileProducer(Object sourceFolder, Object destinationFolder) {
        this.sourceFolder = (String) sourceFolder;
        this.destinationFolder = (String) destinationFolder;
    }

    /*takes files from directory -> uploads via web -> takes page html result -> stores it into folder */
    @Override
    public void run() {
        logger.info("FileProducer: starting, sourceFolder: " + sourceFolder + " destinationFolder: " + destinationFolder);
        try {
            File source = new File(this.sourceFolder);
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
            if(source.isDirectory()) {
                File[] files = source.listFiles();
                for (File file : files) {
                    if (file.isFile()) {
                        processFile(file);
                    }
                }
            }
            else{
                processFile(source);
            }
        } catch (Exception ex) {
            logger.error("Error running FileProducer!", ex);
        }
    }

    private void processFile(File file) { //testing hack for snc stub
        File destinationFile;
        //check if destination is already specified as file or just directory
        if(destination.isDirectory())
            destinationFile = new File(destinationFolder + "/" + file.getName());
        else
            destinationFile = destination;
        if(destinationFile.getName().contains("file_not_stored.xml"))
            return;
        logger.info("FileProducer: moving " + file.getAbsolutePath()  + " -> " + destinationFile);
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
