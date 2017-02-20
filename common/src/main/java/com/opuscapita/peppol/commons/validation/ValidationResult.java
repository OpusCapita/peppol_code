package com.opuscapita.peppol.commons.validation;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationResult implements Serializable {
    private final Archetype validationType;
    private boolean passed;
    private List<ValidationError> errors = new ArrayList<>();

    public ValidationResult(Archetype validationType) {
        this.validationType = validationType;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Archetype getValidationType() {
        return validationType;
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
