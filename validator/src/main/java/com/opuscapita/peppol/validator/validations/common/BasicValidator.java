package com.opuscapita.peppol.validator.validations.common;

import java.util.List;

/**
 * Created by Daniil on 03.05.2016.
 */
public interface BasicValidator {
    List<ValidationError> getErrors();

    public boolean validate(byte[] data);
}
