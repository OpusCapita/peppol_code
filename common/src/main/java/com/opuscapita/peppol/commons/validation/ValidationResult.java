package com.opuscapita.peppol.commons.validation;

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
}
