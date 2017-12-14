package com.opuscapita.peppol.ui.portal.util;

import com.opuscapita.peppol.commons.revised_model.Attempt;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Component
public class FileReprocessor {
    private static final Logger logger = LoggerFactory.getLogger(FileReprocessor.class);

    @Value(value = "${peppol.portal.reprocess.outbound.dir:/tmp}")
    private String reprocessOutboundDir;

    @Value(value = "${peppol.portal.reprocess.inbound.dir:/tmp}")
    private String reprocessInboundDir;


    //new event about file being reprocessed should be handled in transports
    public void reprocessFile(Attempt attempt) {
        try {
            File fileToReprocess = new File(attempt.getFilename());
            File result = new File(attempt.getMessage().isInbound() ? reprocessInboundDir : reprocessOutboundDir, fileToReprocess.getName());
            IOUtils.copy(new FileInputStream(fileToReprocess), new FileOutputStream(result));
            logger.info("Reprocessing, file moved from: " + fileToReprocess.getAbsolutePath() + " to: " + result.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
