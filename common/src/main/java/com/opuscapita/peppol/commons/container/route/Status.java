package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.Nullable;

/**
 * Stores a status of the process, run - not run, what was the outcome, reasons.
 *
 * @author Sergejs.Roze
 */
public enum Status {
    /** Process not yet executed */
    NONE,
    /** Process finished successfully */
    OK,
    /** Process finished with a managed error */
    NOK,
    /** Process failed */
    ERROR;

    private String text;

    @Nullable
    public String getText() {
        return text;
    }

    public Status setText(@Nullable String text) {
        this.text = text;
        return this;
    }
}
