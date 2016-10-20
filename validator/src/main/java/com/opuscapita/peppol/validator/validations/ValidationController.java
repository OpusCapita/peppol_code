package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.validator.util.DocumentContentUtils;
import com.opuscapita.peppol.validator.validations.common.*;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    @Autowired
    ValidatorFactory validatorFactory;

    public ValidationResult validate(@NotNull ContainerMessage containerMessage) {
        Archetype archetype = containerMessage.getBaseDocument().getArchetype();
        BasicValidator validator = null;
        String validatorFetchingError = "";
        try {
            validator = validatorFactory.getValidatorByArchetype(archetype);
        } catch (RuntimeException e) {
            validatorFetchingError = e.getMessage();
        }
        ValidationResult result = new ValidationResult(containerMessage.getBaseDocument().getArchetype());
        result.setPassed(false);
        if (validator != null) {
            byte[] data = new byte[0];
            try {
                data = DocumentContentUtils.getDocumentBytes(getRootDocument(containerMessage));
            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
                addNonTypicalErrorToValidationResult(result, "Validation failed on transforming XML to byte array", e.getMessage());
            }
            List<ValidationError> sbdhValidationErrors = sbdhValidator.performXsdValidation(containerMessage);
            ValidationResult validatorResult = validator.validate(data);
            result.setPassed(validatorResult.isPassed() && sbdhValidationErrors.size() == 0);
            if (!result.isPassed()) {
                sbdhValidationErrors.forEach(result::addError);
                validatorResult.getErrors().forEach(result::addError);
            }
        } else {
            addNonTypicalErrorToValidationResult(result, "Failed to get validator", "No validator found for archetype: " + archetype + "\n\r" + validatorFetchingError);
        }
        return result;
    }

    private Document getRootDocument(@NotNull ContainerMessage containerMessage) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();
        Node importedNode = newDocument.importNode(containerMessage.getBaseDocument().getRootNode(), true);
        newDocument.appendChild(importedNode);
        return newDocument;
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
