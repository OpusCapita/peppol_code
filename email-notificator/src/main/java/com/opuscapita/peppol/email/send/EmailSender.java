package com.opuscapita.peppol.email.send;

import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.prepare.EmailTemplates;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Value("${peppol.email-notificator.sender}")
    private String sender;
    @Value("${peppol.email-notificator.test.recipient:''}")
    private String testRecipient;

    private final JavaMailSender mailSender;

    @SuppressWarnings({"SpringJavaAutowiringInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @Autowired
    public EmailSender(@NotNull JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    void sendMessage(@NotNull CombinedEmail combinedEmail) {
        sendMessage(combinedEmail.getRecipient().getAddresses(), combinedEmail.getCombinedSubject(), EmailTemplates.getEmailFirstLine() + combinedEmail.getCombinedBody());
    }

    private void sendMessage(@NotNull String to, @NotNull String subject, @NotNull String body) {
        String[] recipients;
        String delivered;
        if (StringUtils.isNotBlank(testRecipient)) {
            logger.warn("The list of recipients is: '" + to + "' but the test recipient is set to '" + testRecipient +
                    "' and will be used instead. Remove peppol.email-notificator.test.recipient parameter in order to use real list");
            recipients = getRecipients(testRecipient);
            delivered = testRecipient;
        } else {
            recipients = getRecipients(to);
            delivered = to;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipients);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        logger.info("Error report successfully sent to " + delivered);
    }

    @NotNull
    String[] getRecipients(@NotNull String to) {
        return StringUtils.split(to, " ,;\t");
    }

}
