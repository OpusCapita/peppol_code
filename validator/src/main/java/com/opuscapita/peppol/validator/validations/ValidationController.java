package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentContentUtils;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.commons.validation.util.ValidationErrorBuilder;
import com.opuscapita.peppol.validator.validations.common.SbdhValidator;
import com.opuscapita.peppol.validator.validations.common.ValidatorFactory;
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

    public static final String URN_WWW_CENBII_EU_TRANSACTION_BIICORETRDM010_VER1_0_URN_WWW_PEPPOL_EU_BIS_PEPPOL4A_VER1_0 = "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0";
    @Autowired
    ApplicationContext context;

    @Autowired
    SbdhValidator sbdhValidator;

    @Autowired
    ValidatorFactory validatorFactory;

    public ValidationResult validate(@NotNull ContainerMessage containerMessage) {
        Archetype archetype = containerMessage.getBaseDocument().getArchetype();
        String customizationId = containerMessage.getBaseDocument().getCustomizationId();
        if (archetype == Archetype.UBL && customizationId != null) {
            //Detecting sub-types, like AT or SI
            if (customizationId.contains("erechnung")) {
                archetype = Archetype.AT;
            } else if (customizationId.contains("simplerinvoicing")) {
                archetype = Archetype.SI;
            }

        }
        ValidationResult result = performValidation(containerMessage, archetype);
        return result;
    }

    @NotNull
    private ValidationResult performValidation(@NotNull ContainerMessage containerMessage, Archetype archetype) {
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
