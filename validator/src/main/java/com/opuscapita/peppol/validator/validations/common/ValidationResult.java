package com.opuscapita.peppol.validator.validations.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationResult {
    private final ValidationType validationType;
    boolean passed;
    List<ValidationError> errors = new ArrayList<>();

    public ValidationResult(ValidationType validationType) {
        this.validationType = validationType;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isPassed() {
        return passed;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public void addError(ValidationError error) {
        errors.add(error);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
