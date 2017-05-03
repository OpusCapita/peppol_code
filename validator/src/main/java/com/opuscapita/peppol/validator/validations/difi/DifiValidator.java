package com.opuscapita.peppol.validator.validations.difi;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import no.difi.vefa.validator.DifiValidatorBuilder;
import no.difi.vefa.validator.Validator;
import no.difi.vefa.validator.api.Validation;
import no.difi.vefa.validator.api.ValidatorException;
import no.difi.vefa.validator.source.SimpleDirectorySource;
import no.difi.xsd.vefa.validator._1.AssertionType;
import no.difi.xsd.vefa.validator._1.FlagType;
import no.difi.xsd.vefa.validator._1.Report;
import no.difi.xsd.vefa.validator._1.SectionType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Daniil on 03.05.2016.
 */
public class DifiValidator implements BasicValidator {
    private final static Logger logger = LoggerFactory.getLogger(DifiValidator.class);
    private final Validator validator;

    public DifiValidator(@NotNull DifiValidatorConfig difiValidatorConfig)
            throws ValidatorException {
        Path pathToArtifacts = Paths.get(difiValidatorConfig.getDifiValidationArtifactsPath());
        logger.info("Path to artifacts: " + pathToArtifacts.toString());

        validator = DifiValidatorBuilder.getValidatorInstance(new SimpleDirectorySource(pathToArtifacts));

        // validator.getPackages().forEach(pack -> System.out.println(pack.getUrl()+"-->"+pack.getValue()));
    }

    @NotNull
    @Override
    public ContainerMessage validate(@NotNull ContainerMessage cm, @NotNull byte[] data) {
        Validation validation = validator.validate(new ByteArrayInputStream(data));
        parseErrors(validation.getReport(), cm);
        return cm;
    }

    @SuppressWarnings("ConstantConditions")
    private void parseErrors(Report report, ContainerMessage cm) {
        for (SectionType section : report.getSection()) {
            parseValidationResultSection(cm, section, error -> cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint())), FlagType.ERROR, FlagType.FATAL);
            parseValidationResultSection(cm, section, error -> cm.addWarning(error.toDocumentWarning(cm.getProcessingInfo().getCurrentEndpoint())), FlagType.WARNING);
        }
    }

    protected void parseValidationResultSection(ContainerMessage cm, SectionType section, Consumer<ValidationError> extractedDataConsumer, FlagType... flagTypes) {
        List<FlagType> flagTypesToParseFor = Arrays.asList(flagTypes);
        if (flagTypesToParseFor.contains(section.getFlag())/*section.getFlag() == FlagType.ERROR || section.getFlag() == FlagType.FATAL*/) {
            for (AssertionType assertion : section.getAssertion()) {
                if (flagTypesToParseFor.contains(assertion.getFlag())/*assertion.getFlag() == FlagType.ERROR || assertion.getFlag() == FlagType.FATAL*/) {
                    ValidationError error = new ValidationError();
                    error.withTitle(section.getTitle());
                    error.withIdentifier(assertion.getIdentifier());
                    error.withLocation(assertion.getLocation());
                    error.withFlag(assertion.getFlag().value());
                    error.withText(assertion.getText());
                    error.withTest(assertion.getTest());
                    extractedDataConsumer.accept(error);
                    /*cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));*/
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        validator.close();
    }
}
