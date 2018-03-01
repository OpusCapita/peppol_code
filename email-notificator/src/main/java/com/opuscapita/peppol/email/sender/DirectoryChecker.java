package com.opuscapita.peppol.email.sender;

import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.opuscapita.peppol.email.controller.EmailController.*;

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
    @FileMustExist
    @Value("${peppol.email-notificator.directory}")
    private String directory;
    @FileMustExist
    @Value("${peppol.email-notificator.sent.directory}")
    private String sent;
    @FileMustExist
    @Value("${peppol.email-notificator.failed.directory}")
    private String failed;
    @Value("${peppol.email-notificator.wait.seconds:120}")
    private int seconds;

    @Autowired
    public DirectoryChecker(@NotNull EmailSender emailSender, @NotNull ErrorHandler errorHandler) {
        this.emailSender = emailSender;
        this.errorHandler = errorHandler;
    }

    @Scheduled(fixedRate = 120_000) // 2 minutes
    public void checkDirectory() {
        Date earlier = DateUtils.addSeconds(new Date(), seconds);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);

        Iterator<File> files;
        try {
            files = FileUtils.iterateFiles(new File(directory), ageFileFilter, null);
        } catch (Exception e) {
            logger.error("Failed to check e-mail directory", e);
            errorHandler.reportWithoutContainerMessage(null, e, "Failed to check e-mail directory", null, null);
            return;
        }

        while (files.hasNext()) {
            File next = files.next();
            if (next.getName().endsWith(EXT_TO)) {
                String baseName = FilenameUtils.removeExtension(next.getAbsolutePath());
                String customerId = FilenameUtils.getBaseName(baseName);
                if (filesAvailable(baseName)) {

                    logger.info("Sending an e-mail generated from: " + baseName);
                    String to, subject;
                    List<String> body;
                    try {
                        to = FileUtils.readFileToString(next, Charset.defaultCharset());
                        List<String> subjects = FileUtils.readLines(new File(baseName + EXT_SUBJECT), Charset.defaultCharset());
                        body = FileUtils.readLines(new File(baseName + EXT_BODY), Charset.defaultCharset());
                        subject = normalizeSubjects(subjects);
                    } catch (Exception e) {
                        logger.error("Failed to prepare e-mail message", e);
                        errorHandler.reportWithoutContainerMessage(baseName, e, "Failed to prepare e-mail message", baseName, next.getName());
                        return;
                    }

                    try {
                        emailSender.sendMessage(to, subject, StringUtils.join(body, "\n"));
                        logger.info("E-mail " + baseName + " successfully sent");
                        backupOrDelete(baseName, sent, customerId);
                    } catch (Exception e) {
                        logger.error("Failed to send an e-mail to " + to, e);
                        errorHandler.reportWithoutContainerMessage(customerId, e, "Failed to send an e-mail to " + to, baseName, next.getName());
                        backupOrDelete(baseName, failed, customerId);
                    }
                }
            }
        }
    }

    private boolean filesAvailable(@NotNull String baseName) {
        if (!(new File(baseName + EXT_TO).canWrite())) {
            logger.error("File " + baseName + EXT_TO + " is not accessible, cannot send message");
            return false;
        }
        if (!(new File(baseName + EXT_SUBJECT).canWrite())) {
            logger.error("File " + baseName + EXT_SUBJECT + " is not accessible, cannot send message");
            return false;
        }
        if (!(new File(baseName + EXT_BODY).canWrite())) {
            logger.error("File " + baseName + EXT_BODY + " is not accessible, cannot send message");
            return false;
        }
        return true;
    }

    void backupOrDelete(@NotNull String baseName, @NotNull String directory, @NotNull String customerId) {
        if (StringUtils.isBlank(directory)) {
            try {
                delete(baseName + EXT_TO);
                delete(baseName + EXT_SUBJECT);
                delete(baseName + EXT_BODY);
                logger.info("Deleted processed files related to " + baseName);
            } catch (Exception e) {
                logger.error("Failed to delete e-mail files about " + baseName);
                errorHandler.reportWithoutContainerMessage(
                        customerId, e, "Failed to delete e-mail files about " + baseName, baseName, baseName + EXT_TO);
            }
        } else {
            try {
                File destination = new File(directory);
                moveOrAppend(new File(baseName + EXT_TO), destination);
                moveOrAppend(new File(baseName + EXT_SUBJECT), destination);
                moveOrAppend(new File(baseName + EXT_BODY), destination);
                logger.info("Files for " + baseName + " moved to directory " + destination);
            } catch (Exception e) {
                logger.error("Failed to move e-mails to " + directory, e);
                errorHandler.reportWithoutContainerMessage(
                        baseName, e, "Failed to move e-mails to " + directory, baseName, baseName + EXT_TO);
            }
        }
    }

    private void delete(@NotNull String fileName) throws IOException {
        boolean deleted = new File(fileName).delete();
        if (!deleted) {
            logger.error("Failed to delete file: " + fileName);
            throw new IOException("Failed to delete file: " + fileName);
        }
    }

    void moveOrAppend(@NotNull File source, @NotNull File directory) throws IOException {
        File result = new File(directory, source.getName());
        if (result.exists()) {
            Files.write(result.toPath(), "\n\n".getBytes(), StandardOpenOption.APPEND);
            Files.write(result.toPath(), Files.readAllLines(source.toPath()), StandardOpenOption.APPEND);
            delete(source.getAbsolutePath());
        } else {
            FileUtils.moveFileToDirectory(source, directory, true);
        }
    }

    private String normalizeSubjects(List<String> subjects) {
        return subjects.stream().distinct().collect(Collectors.joining("; "));
    }

    void setDirectory(@NotNull String directory) {
        this.directory = directory;
    }

}
