package com.opuscapita.peppol.email.sender;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class EmailSender {
    @Value("${email.sender?:'peppol@opuscapita.com'}")
    private String sender;

    @Value("${email.smtp.server}")
    private String smtpServer;

    @Value("${email.smtp.port?:25}")
    private int smtpPort;

    private final MailSender mailSender;
    private final SimpleMailMessage template;

    @Autowired
    public EmailSender(@NotNull MailSender mailSender, @NotNull SimpleMailMessage template) {
        this.mailSender = mailSender;
        this.template = template;
    }

    public void sendMessage(@NotNull String to, @NotNull String subject, @NotNull String body) {
        SimpleMailMessage message = new SimpleMailMessage(template);
        message.setTo(to.split(","));
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
