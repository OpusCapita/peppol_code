package com.opuscapita.peppol.email.sender;

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
public class DirectoryChecker {
    private final static Logger logger = LoggerFactory.getLogger(DirectoryChecker.class);
    private final EmailSender emailSender;
    @Value("${peppol.email-notificator.directory}")
    private String directory;
    @Value("${peppol.email-notificator.sent.directory:}")
    private String sent;
    @Value("${peppol.email-notificator.wait.seconds:120}")
    private int seconds;

    @Autowired
    public DirectoryChecker(@NotNull EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Scheduled(fixedRate = 120_000) // 2 minutes
    public void checkDirectory() throws IOException {
        Date earlier = DateUtils.addSeconds(new Date(), seconds);
        AgeFileFilter ageFileFilter = new AgeFileFilter(earlier);

        Iterator<File> files = FileUtils.iterateFiles(new File(directory), ageFileFilter, null);
        while (files.hasNext()) {
            File next = files.next();
            if (next.getName().endsWith(EXT_TO)) {
                String baseName = FilenameUtils.removeExtension(next.getAbsolutePath());
                logger.info("Sending an e-mail generated from: " + baseName);

                String to = FileUtils.readFileToString(next, Charset.defaultCharset());
                List<String> subjects = FileUtils.readLines(new File(baseName + EXT_SUBJECT), Charset.defaultCharset());
                List<String> body = FileUtils.readLines(new File(baseName + EXT_BODY), Charset.defaultCharset());
                String subject = normalizeSubjects(subjects);
                emailSender.sendMessage(to, subject, StringUtils.join(body, "\n"));
                logger.info("E-mail " + baseName + " successfully sent");

                if (StringUtils.isNotBlank(sent)) {
                    File destination = new File(sent);
                    try {
                        moveOrAppend(new File(baseName + EXT_TO), destination);
                        moveOrAppend(new File(baseName + EXT_SUBJECT), destination);
                        moveOrAppend(new File(baseName + EXT_BODY), destination);
                        logger.info("Files for " + baseName + " moved to backup directory " + destination);
                    } catch (Exception e) {
                        logger.error("Failed to create backup of sent e-mails in " + destination, e);
                        throw new IOException("Failed to create backup of sent e-mails in " + destination, e);
                    }
                } else {
                    delete(baseName + EXT_TO);
                    delete(baseName + EXT_SUBJECT);
                    delete(baseName + EXT_BODY);
                }
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
        } else {
            FileUtils.moveFileToDirectory(source, directory, true);
        }
    }

    private String normalizeSubjects(List<String> subjects) {
        return subjects.stream().distinct().collect(Collectors.joining("; "));
    }

}
