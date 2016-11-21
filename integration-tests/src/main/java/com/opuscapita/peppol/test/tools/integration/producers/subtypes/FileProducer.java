package com.opuscapita.peppol.test.tools.integration.producers.subtypes;

import com.opuscapita.peppol.test.tools.integration.producers.Producer;
import org.apache.log4j.LogManager;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by gamanse1 on 2016.11.14..
 */
public class FileProducer implements Producer {
    private final static org.apache.log4j.Logger logger = LogManager.getLogger(FileProducer.class);
    private String sourceFileName;
    private String destinationFileName;

    public FileProducer(String sourceFile, String destinationFile) {
        this.sourceFileName = sourceFile;
        this.destinationFileName = destinationFile;
    }
    /*takes files from directory -> uploads via web -> takes page html result -> stores it into folder */
    @Override
    public void run() {
        try{
            File sourceFile = new File(this.sourceFileName);
            if(!sourceFile.exists()){
                logger.error(this.sourceFileName + " doesn't exist!");
                return;
            }
            Path source = Paths.get(this.sourceFileName);
            Path destination = Paths.get(destinationFileName);

            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex){
            logger.error("Error running FileProducer!", ex);
        }
    }
}
