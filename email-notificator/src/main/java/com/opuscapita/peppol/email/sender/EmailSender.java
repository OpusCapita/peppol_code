package com.opuscapita.peppol.email.sender;

import org.jetbrains.annotations.NotNull;
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
    @Value("${peppol.email-notificator.sender}")
    private String sender;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailSender(@NotNull JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMessage(@NotNull String to, @NotNull String subject, @NotNull String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(to.split(","));
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

}
