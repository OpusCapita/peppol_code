package com.opuscapita.peppol.validator.validations.common;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationError {
    private String title;
    private String details;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "details='" + details + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
