package com.opuscapita.peppol.commons.validation.util;

import com.opuscapita.peppol.commons.validation.ValidationError;

/**
 * Created by bambr on 16.22.9.
 */
public final class ValidationErrorBuilder {
    private String title;
    private String details;

    private ValidationErrorBuilder() {
    }

    public static ValidationErrorBuilder aValidationError() {
        return new ValidationErrorBuilder();
    }

    public ValidationErrorBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ValidationErrorBuilder withDetails(String details) {
        this.details = details;
        return this;
    }

    public ValidationError build() {
        ValidationError validationError = new ValidationError();
        validationError.setTitle(title);
        validationError.setDetails(details);
        return validationError;
    }
}
