package com.opuscapita.peppol.email.model;

import org.jetbrains.annotations.NotNull;

/**
 * Two types of recipients are supported - Access Points and Customers. Both have common fields like ID and name.
 */
public class Recipient {
    public enum Type {
        AP, CUSTOMER
    }

    private final Type type;
    private final String recipientId;
    private final String recipientName;
    private final String addresses;

    public Recipient(@NotNull Type type, @NotNull String recipientId, @NotNull String recipientName, @NotNull String addresses) {
        this.type = type;
        this.recipientId = recipientId;
        this.recipientName = recipientName;
        this.addresses = addresses;
    }

    @NotNull
    public Type getType() {
        return type;
    }

    @NotNull
    public String getId() {
        return recipientId;
    }

    @NotNull
    public String getName() {
        return recipientName;
    }

    public String getAddresses() {
        return addresses;
    }

}
