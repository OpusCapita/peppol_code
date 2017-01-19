package com.opuscapita.peppol.validator.validations.difi;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.source.SimpleDirectorySource;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.SectionType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daniil on 03.05.2016.
 */
public class DifiValidator implements BasicValidator {
    private final Validator validator;
    private final DifiValidatorConfig difiValidatorConfig;


    public DifiValidator(DifiValidatorConfig difiValidatorConfig) throws ValidatorException {
        this.difiValidatorConfig = difiValidatorConfig;
        validator = ValidatorBuilder.newValidator().setSource(new SimpleDirectorySource(Paths.get(difiValidatorConfig.getDifiValidationArtifactsPath()))).build();
        validator.getPackages().forEach(pack -> System.out.println(pack.getUrl()+"-->"+pack.getValue()));
    }

    @Override
    public ValidationResult validate(byte[] data) {
        ValidationResult result = new ValidationResult(Archetype.UBL);
        List<ValidationError> errors = new ArrayList<>();
        try {
            Validation validation = validator.validate(new ByteArrayInputStream(data));
            result.setPassed(validation.getReport().getFlag() != FlagType.ERROR && validation.getReport().getFlag() != FlagType.FATAL);
            if (!result.isPassed()) {
                validation.getReport()
                        .getSection()
                        .stream()
                        .filter(raw -> raw.getFlag() == FlagType.ERROR || raw.getFlag() == FlagType.FATAL)
                        .forEach(section -> {
                            ValidationError error = new ValidationError();
                            error.setTitle(section.getTitle());
                            error.setDetails(extractErrorDetails(section));
                            errors.add(error);
                        }
                        );
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
            result.setPassed(false);
        }
        errors.forEach(result::addError);
        return result;
    }

    private String extractErrorDetails(SectionType section) {
        return section.getAssertion().stream().filter(raw -> raw.getFlag() == FlagType.ERROR || raw.getFlag() == FlagType.FATAL).map(filtered -> filtered.getIdentifier() + " " + filtered.getLocation() + " " + filtered.getTest() + " " + filtered.getText()).collect(Collectors.joining("\n\r"));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        validator.close();
    }
}
