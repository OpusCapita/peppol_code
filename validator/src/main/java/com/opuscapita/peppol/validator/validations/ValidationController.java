package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.validator.util.DocumentContentUtils;
import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.SbdhValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationError;
import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import com.opuscapita.peppol.validator.validations.difi.DifiValidator;
import com.opuscapita.peppol.validator.validations.svefaktura1.SveFaktura1Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.xml.transform.TransformerException;
import java.util.List;

/**
 * Created by bambr on 16.5.8.
 */
@Component
public class ValidationController {

    @Autowired
    ApplicationContext context;

    @Autowired
    SbdhValidator sbdhValidator;

    public ValidationResult validate(ContainerMessage containerMessage) {
        Archetype archetype = containerMessage.getBaseDocument().getArchetype();
        BasicValidator validator = getValidator(archetype);
        ValidationResult result = new ValidationResult(containerMessage.getBaseDocument().getArchetype());
        result.setPassed(false);
        if (validator != null) {
            byte[] data = new byte[0];
            try {
                data = DocumentContentUtils.getDocumentBytes(containerMessage);
            } catch (TransformerException e) {
                e.printStackTrace();
                addNonTypicalErrorToValidationResult(result, "Validation failed on transforming XML to byte array", e.getMessage());
            }
            List<ValidationError> sbdhValidationErrors = sbdhValidator.performXsdValidation(containerMessage);
            result.setPassed(validator.validate(data) && sbdhValidationErrors.size() == 0);
            if (!result.isPassed()) {
                sbdhValidationErrors.forEach(result::addError);
                validator.getErrors().forEach(result::addError);
            }
        } else {
            addNonTypicalErrorToValidationResult(result, "Failed to get validator", "No validator found for archetype: " + archetype);
        }
        return result;
    }

    private BasicValidator getValidator(Archetype archetype) {
        BasicValidator validator = null;
        switch (archetype) {
            case SVEFAKTURA1:
                validator = context.getBean(SveFaktura1Validator.class);
                break;
            case INVALID:

                break;
            case UBL:
            default:
                validator = context.getBean(DifiValidator.class);
        }
        return validator;
    }

    private void addNonTypicalErrorToValidationResult(ValidationResult validationResult, String title, String details) {
        validationResult.addError(
                ValidationErrorBuilder
                        .aValidationError()
                        .withTitle(title)
                        .withDetails(details)
                        .build()
        );
    }


}
