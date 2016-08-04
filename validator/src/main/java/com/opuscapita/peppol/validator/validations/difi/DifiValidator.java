package com.opuscapita.peppol.validator.validations.difi;

import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationError;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniil on 03.05.2016.
 */
@Component
@Qualifier("difi")
public class DifiValidator implements BasicValidator {
    private final Validator validator;
    List<ValidationError> errors = new ArrayList<>();

    public DifiValidator() throws ValidatorException {
        validator = ValidatorBuilder.newValidator().build();
    }

    @Override
    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public boolean validate(byte[] data) {
        boolean result = false;
        try {
            errors.clear();

            Validation validation = validator.validate(new ByteArrayInputStream(data));
            result = validation.getReport().getFlag() != FlagType.ERROR && validation.getReport().getFlag() != FlagType.FATAL;
            if(!result) {
                validation.getReport().getSection().stream().filter(raw -> raw.getFlag() == FlagType.ERROR || raw.getFlag() == FlagType.FATAL).forEach(section -> {
                    ValidationError error = new ValidationError();
                    error.setTitle(section.getTitle());
                    error.setDetails(extractErrorDetails(section));
                    errors.add(error);
                });
            }
        } catch (NullPointerException e) {
            ValidationError error = new ValidationError();
            error.setTitle(e.getClass().getCanonicalName());
            PrintStream writer = new PrintStream(new ByteArrayOutputStream());
            e.printStackTrace(writer);
            writer.flush();
            error.setDetails(writer.toString());
            writer.close();
            errors.add(error);
            result = false;
        }
        return result;
    }

    private String extractErrorDetails(SectionType section) {
        return section.getAssertion().stream().filter(raw -> raw.getFlag() == FlagType.ERROR || raw.getFlag() == FlagType.FATAL).map(filtered -> filtered.getIdentifier() + " " + filtered.getLocation() + " " + filtered.getTest() + " " + filtered.getText()).collect(Collectors.joining("\n\r"));
    }
}
