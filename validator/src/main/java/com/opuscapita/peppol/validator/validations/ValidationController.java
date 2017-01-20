package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentContentUtils;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.container.document.impl.InvalidDocument;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.common.SbdhValidator;
import com.opuscapita.peppol.validator.validations.common.ValidatorFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final static Logger logger = LoggerFactory.getLogger(ValidationController.class);

    private final SbdhValidator sbdhValidator;
    private final ValidatorFactory validatorFactory;

    @Autowired
    public ValidationController(@NotNull SbdhValidator sbdhValidator, @NotNull ValidatorFactory validatorFactory) {
        this.sbdhValidator = sbdhValidator;
        this.validatorFactory = validatorFactory;
    }

    public ValidationResult validate(@NotNull ContainerMessage containerMessage) {
        if (containerMessage.getBaseDocument() == null) {
            throw new IllegalArgumentException("Document is null for " + containerMessage.getFileName());
        }

        Archetype archetype = containerMessage.getBaseDocument().getArchetype();
        String customizationId = containerMessage.getBaseDocument().getCustomizationId();
        if (archetype == Archetype.UBL) {
            //Detecting sub-types, like AT or SI
            if (customizationId.contains("erechnung")) {
                archetype = Archetype.AT;
            } else if (customizationId.contains("simplerinvoicing")) {
                archetype = Archetype.SI;
            }

        }
        return performValidation(containerMessage, archetype);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private ValidationResult performValidation(@NotNull ContainerMessage containerMessage, Archetype archetype) {
        BasicValidator validator;
        try {
            validator = validatorFactory.getValidatorByArchetype(archetype);
        } catch (Exception e) {
            String validatorFetchingError = e.getMessage();
            throw new IllegalArgumentException("No validator defined for archetype " + archetype + ", error: " + validatorFetchingError);
        }

        ValidationResult result = new ValidationResult(containerMessage.getBaseDocument().getArchetype());
        result.setPassed(false);

        byte[] data;
        try {
            data = DocumentContentUtils.getDocumentBytes(getRootDocument(containerMessage));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new IllegalArgumentException("Validation failed during XML transformation", e);
        }

        List<ValidationError> sbdhValidationErrors = sbdhValidator.performXsdValidation(containerMessage);
        ValidationResult validatorResult = validator.validate(data);
        result.setPassed(validatorResult.isPassed() && sbdhValidationErrors.size() == 0);
        if (!result.isPassed()) {
            sbdhValidationErrors.forEach(result::addError);
            validatorResult.getErrors().forEach(result::addError);
        }
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    private Document getRootDocument(@NotNull ContainerMessage containerMessage) throws ParserConfigurationException {
        if (containerMessage.getBaseDocument() instanceof InvalidDocument) {
            throw new IllegalArgumentException("Unable to validate invalid documents");
        }
        if (containerMessage.getBaseDocument().getRootNode() == null) {
            throw new IllegalArgumentException("No root node in the document");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();

        Node importedNode = newDocument.importNode(containerMessage.getBaseDocument().getRootNode(), true);
        newDocument.appendChild(importedNode);
        return newDocument;
    }

}
