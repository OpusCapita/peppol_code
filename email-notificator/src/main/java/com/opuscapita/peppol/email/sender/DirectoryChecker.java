package com.opuscapita.peppol.email.sender;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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

    @Value("${email.directory}")
    private String directory;

    @Value("${email.sent.directory?:''}")
    private String sent;

    @Value("${email.wait.seconds?:120")
    private int seconds;

    private final EmailSender emailSender;

    @Autowired
    public DirectoryChecker(EmailSender emailSender) {
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

                String to = FileUtils.readFileToString(next, Charset.defaultCharset());
                List<String> subjects = FileUtils.readLines(new File(baseName + EXT_SUBJECT), Charset.defaultCharset());
                List<String> body = FileUtils.readLines(new File(baseName + EXT_BODY), Charset.defaultCharset());
                String subject = normalizeSubjects(subjects);
                emailSender.sendMessage(to, subject, StringUtils.join(body, "\n"));
            }
        }
    }

    private String normalizeSubjects(List<String> subjects) {
        return subjects.stream().distinct().collect(Collectors.joining("; "));
    }

}
