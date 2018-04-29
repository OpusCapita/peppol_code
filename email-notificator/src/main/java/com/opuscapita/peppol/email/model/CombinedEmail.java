package com.opuscapita.peppol.email.model;

import com.opuscapita.peppol.email.prepare.BodyFormatter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sergejs.Roze
 */
public class CombinedEmail {
    private final String recipients;
    private final List<SingleEmail> mails = new ArrayList<>();
    private final boolean createTicket;
    private final String customerId;

    public CombinedEmail(@NotNull String recipients, boolean createTicket, @Nullable String customerId) {
        this.recipients = recipients;
        this.createTicket = createTicket;
        this.customerId = customerId;
    }

    public void addMail(@NotNull SingleEmail singleEmail) {
        mails.add(singleEmail);
    }

    @Nullable
    public String getCustomerId() {
        return customerId;
    }

    @NotNull
    public String getRecipients() {
        return recipients;
    }

    public boolean isCreateTicket() {
        return createTicket;
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

    public String getTicket() {
        String result = BodyFormatter.getTicketHeader();
        result += "TO: " + getRecipients();
        result += "\nSUBJECT: " + getCombinedSubject();
        result += "\nBODY:\n\n" + getCombinedBody();
        return result;
    }

    public String getRecipientId() {
        return mails.stream().map(SingleEmail::getRecipient).findAny().orElse("unknown");
    }

    public String getSenderId() {
        return mails.stream().map(SingleEmail::getSender).findAny().orElse("unknown");
    }
}
