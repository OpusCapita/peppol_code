package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationResult {
    private final Archetype validationType;
    boolean passed;
    List<ValidationError> errors = new ArrayList<>();

    public ValidationResult(Archetype validationType) {
        this.validationType = validationType;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
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
}
