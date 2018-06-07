package com.opuscapita.peppol.email.model;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class SingleEmail {
    private final String subject;
    private final String body;
    private final ContainerMessage cm;

    public SingleEmail(@NotNull ContainerMessage cm, @NotNull String subject, @NotNull String body) {
        this.cm = cm;
        this.subject = subject;
        this.body = body;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }

    @NotNull
    public String getFileName() {
        return cm.getFileName();
    }

    @NotNull
    public String getSender() {
        return cm.getDocumentInfo() == null ? "unknown" : cm.getDocumentInfo().getSenderId();
    }

    @NotNull
    public String getRecipient() {
        return cm.getDocumentInfo() == null ? "unknown" : cm.getDocumentInfo().getRecipientId();
    }

    @NotNull
    public String getBody() {
        return body;
    }

    @NotNull
    public ContainerMessage getContainerMessage() {
        return cm;
    }

}
