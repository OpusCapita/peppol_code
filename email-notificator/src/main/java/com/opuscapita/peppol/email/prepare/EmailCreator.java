package com.opuscapita.peppol.email.prepare;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.template.bean.FileMustExist;
import com.opuscapita.peppol.commons.template.bean.ValuesChecker;
import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.model.Recipient;
import com.opuscapita.peppol.email.model.SingleEmail;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class EmailCreator extends ValuesChecker {
    private static final Logger logger = LoggerFactory.getLogger(EmailCreator.class);

    @FileMustExist
    @Value("${peppol.email-notificator.directory}")
    private String directory;

    private final ErrorHandler errorHandler;
    private final Gson gson;

    @Autowired
    public EmailCreator(@NotNull ErrorHandler errorHandler, @NotNull Gson gson) {
        this.errorHandler = errorHandler;
        this.gson = gson;
    }

    String create(@NotNull Recipient recipient, @NotNull ContainerMessage cm, @NotNull String subject, @NotNull String body, boolean createTicket)
            throws IOException {
        SingleEmail singleEmail = new SingleEmail(cm, subject, body);

        String fileName = recipient.getType().name() + "_" + recipient.getId();
        File combined = new File(directory, fileName + ".json");
        if (combined.exists()) {
            add(combined, singleEmail);
        } else {
            create(combined, singleEmail, recipient, createTicket);
        }
        return combined.getAbsolutePath();
    }

    private void add(File file, SingleEmail singleEmail) throws IOException {
        CombinedEmail combinedEmail;
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            combinedEmail = gson.fromJson(reader, CombinedEmail.class);
            combinedEmail.addMail(singleEmail);
        }
        String json = gson.toJson(combinedEmail);
        FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        logger.info("Mail about " + singleEmail.getFileName() + " successfully added to existing file " + file.getAbsolutePath());
    }

    private void create(File file, SingleEmail singleEmail, Recipient recipient, boolean createTicket) throws IOException {
        CombinedEmail combinedEmail = new CombinedEmail(createTicket, recipient);
        combinedEmail.addMail(singleEmail);
        String json = gson.toJson(combinedEmail);
        FileUtils.writeStringToFile(file, json, Charset.defaultCharset());
        logger.info("Created new e-mail file for " + singleEmail.getFileName() + ": " + file.getAbsolutePath());
    }

    void fail(@NotNull ContainerMessage cm, @NotNull String message, @Nullable String details) {
        logger.warn(message);
        EventingMessageUtil.reportEvent(cm, "Email notificator failure. " + message);
        errorHandler.reportWithContainerMessage(cm, null, message, details);
    }

    void setDirectory(String directory) {
        this.directory = directory;
    }
}
