package com.opuscapita.peppol.email.model;

import com.opuscapita.peppol.email.prepare.EmailTemplates;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * We're trying to stockpile e-mails before sending those out. It is implemented to improve batch processing,
 * sending one e-mail per batch instead of a single e-mail per file.
 *
 * This class represents this stockpile (or collection) of {@link SingleEmail} objects.
 *
 * @author Sergejs.Roze
 */
public class CombinedEmail {
    private final List<SingleEmail> mails = new ArrayList<>();
    private final boolean ticketRequested;
    private final Recipient recipient;

    public CombinedEmail(boolean ticketRequested, @NotNull Recipient recipient) {
        this.ticketRequested = ticketRequested;
        this.recipient = recipient;
    }

    public void addMail(@NotNull SingleEmail singleEmail) {
        mails.add(singleEmail);
    }

    @NotNull
    public List<SingleEmail> getMails() {
        return mails;
    }

    public boolean isTicketRequested() {
        return ticketRequested;
    }

    @NotNull
    public Recipient getRecipient() {
        return recipient;
    }

    private String getFiles() {
        return mails.stream().map(SingleEmail::getFileName).collect(Collectors.joining(", "));
    }

    public String getCombinedSubject() {
        return mails.stream()
                .map(SingleEmail::getSubject)
                .distinct()
                .collect(Collectors.joining("; "));
    }

    public String getCombinedBody() {
        return mails.stream()
                .map(SingleEmail::getBody)
                .collect(Collectors.joining("\n\n"));
    }

    public String getTicketHeader() {
        if (recipient.getType() == Recipient.Type.AP) {
            return EmailTemplates.getTicketHeader("AP: " + recipient.getId(), recipient.getName());
        }
        return EmailTemplates.getTicketHeader("Customer: " + recipient.getId(), recipient.getName());
    }

    public String getTicketBody() {
        String result = EmailTemplates.getTicketFirstLine(recipient, getFiles()) + "\n";
        result += "\nABOUT: " + getFiles();
        result += "\nTO: " + recipient.getAddresses();
        result += "\nSUBJECT: " + getCombinedSubject();
        result += "\nBODY:\n\n" + getCombinedBody();
        return result;
    }

}
