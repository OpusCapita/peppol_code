package com.opuscapita.peppol.email.send;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.prepare.BodyFormatter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Date;
import java.util.Iterator;

/**
 * Checks a directory on a periodical basis for email messages waiting to be sent.
 *
 * @author Sergejs.Roze
 */
@Component
public class DirectoryChecker extends ValuesChecker {
    private final static Logger logger = LoggerFactory.getLogger(DirectoryChecker.class);

    private final EmailSender emailSender;
    private final ErrorHandler errorHandler;
    private final Storage storage;
    private final Gson gson;

    @FileMustExist
    @Value("${peppol.email-notificator.directory}")
    private String directory;
    @Value("${peppol.email-notificator.wait.seconds:120}")
    private int seconds;

    @Autowired
    public DirectoryChecker(@NotNull EmailSender emailSender, @NotNull ErrorHandler errorHandler, @NotNull Storage storage, @NotNull Gson gson) {
        this.emailSender = emailSender;
        this.errorHandler = errorHandler;
        this.storage = storage;
        this.gson = gson;
    }

    @Scheduled(fixedRate = 120_000) // 2 minutes
    public void checkDirectory() {
        Date earlier = DateUtils.addSeconds(new Date(), -seconds);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);

        Iterator<File> files;
        try {
            files = FileUtils.iterateFiles(new File(directory), ageFileFilter, null);
        } catch (Exception e) {
            logger.error("Failed to read a content of e-mail directory: " + directory, e);
            errorHandler.reportWithoutContainerMessage(null, e, "Failed to read a content of e-mail directory: " + directory,
                    null, null);
            return;
        }

        while (files.hasNext()) {
            File next = files.next();
            if (next.getName().endsWith(".json")) {
                logger.info("Sending an e-mail generated from: " + next.getAbsolutePath());

                CombinedEmail combinedEmail;
                try (Reader reader = new BufferedReader(new FileReader(next))) {
                    combinedEmail = gson.fromJson(reader, CombinedEmail.class);
                } catch (Exception e) {
                    String message = "Failed to read and process JSON file " + next.getAbsolutePath();
                    logger.error(message, e);
                    errorHandler.reportWithoutContainerMessage(null, e, message, next.getAbsolutePath(), next.getAbsolutePath());
                    backup(next, "unknown", "unknown");
                    continue;
                }

                try {
                    emailSender.sendMessage(combinedEmail);
                } catch (Exception e) {
                    String message = "Failed to send e-mail generated from " + next.getAbsolutePath();
                    logger.error(message, e);
                    errorHandler.reportWithoutContainerMessage(combinedEmail.getCustomerId(), e, message, next.getAbsolutePath(), next.getAbsolutePath());
                    backup(next, combinedEmail.getSenderId(), combinedEmail.getRecipientId());
                    continue;
                }

                if (combinedEmail.isCreateTicket()) {
                    logger.info("Creating ticket about successfully sent e-mail for " + next.getAbsolutePath());
                    errorHandler.reportWithoutContainerMessage(combinedEmail.getCustomerId(), null, BodyFormatter.getTicketHeader(),
                            next.getAbsolutePath(), next.getAbsolutePath(), combinedEmail.getCombinedBody());
                }

                backup(next, combinedEmail.getSenderId(), combinedEmail.getRecipientId());
            }
        }
    }

    private void backup(@NotNull File file, String senderId, String recipientId) {
        try {
            String result = storage.moveToLongTerm(senderId, recipientId, file);
            logger.info("Email file " + file.getAbsolutePath() + " moved to " + result);
        } catch (Exception e) {
            String message = "Failed to move JSON file " + file.getAbsolutePath() + " to long term storage";
            logger.error(message, e);
            errorHandler.reportWithoutContainerMessage(null, e, message, file.getAbsolutePath(), file.getAbsolutePath());
        }
    }

}
