package com.opuscapita.peppol.validator.validations.difi;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import no.difi.vefa.validator.DifiValidatorBuilder;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.ValidatorBuilder;
import no.difi.vefa.validator.api.Properties;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.properties.SimpleProperties;
import no.difi.vefa.validator.source.SimpleDirectorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.ClassUtils;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniil on 03.05.2016.
 */
public class DifiValidator implements BasicValidator {
    private final Validator validator;

    public DifiValidator(@NotNull DifiValidatorConfig difiValidatorConfig) throws ValidatorException {
        Path pathToArtifacts = Paths.get(difiValidatorConfig.getDifiValidationArtifactsPath());
        System.out.println("Path to artifacts: " + pathToArtifacts.toString());


        validator = DifiValidatorBuilder.getValidatorInstance(new SimpleDirectorySource(pathToArtifacts));/*ValidatorBuilder.newValidator().setSource(
                new SimpleDirectorySource(pathToArtifacts)).build();*/

        // validator.getPackages().forEach(pack -> System.out.println(pack.getUrl()+"-->"+pack.getValue()));
    }

    @Override
    public ValidationResult validate(byte[] data) {
        ValidationResult result = new ValidationResult(Archetype.UBL);
        List<ValidationError> errors;
        Validation validation = validator.validate(new ByteArrayInputStream(data));
        result.setPassed(validation.getReport().getFlag() != FlagType.ERROR && validation.getReport().getFlag() != FlagType.FATAL);
        if (!result.isPassed()) {
            errors = parseErrors(validation.getReport());
            errors.forEach(result::addError);
        }
        return result;
    }

    private List<ValidationError> parseErrors(Report report) {
        List<ValidationError> errors = new ArrayList<>();

        for (SectionType section : report.getSection()) {
            if (section.getFlag() == FlagType.ERROR || section.getFlag() == FlagType.FATAL) {
                for (AssertionType assertion : section.getAssertion()) {
                    if (assertion.getFlag() == FlagType.ERROR || assertion.getFlag() == FlagType.FATAL) {
                        ValidationError error = new ValidationError();
                        error.withTitle(section.getTitle());
                        error.withIdentifier(assertion.getIdentifier());
                        error.withLocation(assertion.getLocation());
                        error.withFlag(assertion.getFlag().value());
                        error.withText(assertion.getText());
                        error.withTest(assertion.getTest());
                        errors.add(error);
                    }
                }
            }
        }

        return errors;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        validator.close();
    }
}
