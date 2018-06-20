package com.opuscapita.peppol.support.ui.controller;

import org.apache.commons.lang3.ArrayUtils;

@SuppressWarnings("unused")
public class EmailNotificatorStatus {
    private final boolean inboundCustomerEmailEnabled;
    private final boolean inboundApEmailEnabled;
    private final boolean outboundCustomerEmailEnabled;

    @SuppressWarnings("WeakerAccess")
    public EmailNotificatorStatus(String[] inboundActions, String[] outboundActions) {
        inboundCustomerEmailEnabled = ArrayUtils.contains(inboundActions, EmailActions.EMAIL_CUSTOMER);
        inboundApEmailEnabled = ArrayUtils.contains(inboundActions, EmailActions.EMAIL_AP);
        outboundCustomerEmailEnabled = ArrayUtils.contains(outboundActions, EmailActions.EMAIL_CUSTOMER);
    }

    public boolean isInboundCustomerEmailEnabled() {
        return inboundCustomerEmailEnabled;
    }

    public boolean isInboundApEmailEnabled() {
        return inboundApEmailEnabled;
    }

    public boolean isOutboundCustomerEmailEnabled() {
        return outboundCustomerEmailEnabled;
    }
}
