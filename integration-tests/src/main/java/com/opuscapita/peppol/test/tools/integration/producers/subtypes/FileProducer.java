package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class FileProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileProducer.class);
    private String sourceFolder;
    private String destinationFolder;

    public FileProducer(Object sourceFolder, Object destinationFolder) {
        this.sourceFolder = (String) sourceFolder;
        this.destinationFolder = (String) destinationFolder;
    }

    /*takes files from directory -> uploads via web -> takes page html result -> stores it into folder */
    @Override
    public void run() {
        try {
            File folder = new File(this.sourceFolder);
            if (!folder.exists()) {
                logger.error(this.sourceFolder + " doesn't exist!");
                return;
            }
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    processFile(file);
                }
            }
        } catch (Exception ex) {
            logger.error("Error running FileProducer!", ex);
        }
    }

    private void processFile(File file) {
        File destinationFile = new File(destinationFolder + "\\" + file.getName());
        //if test file already exists
        if (destinationFile.exists())
            return;
        try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
            fos.write(Files.readAllBytes(file.toPath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
