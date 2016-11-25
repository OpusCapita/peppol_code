package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class FileProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileProducer.class);
    private String sourceFolder;
    private String destinationFileName;

    public FileProducer(Object sourceFolder, Object destinationFile) {
        this.sourceFolder = (String) sourceFolder;
        this.destinationFileName = (String) destinationFile;
    }
    /*takes files from directory -> uploads via web -> takes page html result -> stores it into folder */
    @Override
    public void run() {
        try{
            File folder = new File(this.sourceFolder);
            if (!folder.isDirectory()) {
                logger.error(this.sourceFolder + " doesn't exist!");
                return;
            }
            File[] files = folder.listFiles();
            for (File file : files) {
                if (!file.isFile()) {
                    processFile(file);
                }
            }
        } catch (Exception ex){
            logger.error("Error running FileProducer!", ex);
        }
    }

    private void processFile(File file) {
        ContainerMessage container;
    }
}
