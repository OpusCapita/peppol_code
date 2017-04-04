package com.opuscapita.peppol.commons.validation;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationResult implements Serializable {
    private boolean passed;
    private List<ValidationError> errors = new ArrayList<>();

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Returns all errors in a single line.
     *
     * @return all errors in a single line
     */
    public @NotNull String getErrorsString() {
        return errors.stream()
                .map(ValidationError::toString)
                .collect(Collectors.joining("; "));
    }
}
