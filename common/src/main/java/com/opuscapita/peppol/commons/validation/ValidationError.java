package com.opuscapita.peppol.commons.validation;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationError {
    private String title;
    private String details;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "details='" + details + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
