package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.Archetype;
import com.opuscapita.peppol.commons.container.document.DocumentContentUtils;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.validator.ValidationController;
import com.opuscapita.peppol.validator.validations.common.SbdhValidator;
import com.opuscapita.peppol.validator.validations.common.ValidatorFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by bambr on 16.5.8.
 */
@Component
@Lazy
@ConditionalOnProperty(name = "peppol.validator.difi.enabled", havingValue = "true", matchIfMissing = true)
public class DifiValidationController implements ValidationController {
    public static final String URN_WWW_CENBII_EU_TRANSACTION_BIICORETRDM010_VER1_0_URN_WWW_PEPPOL_EU_BIS_PEPPOL4A_VER1_0 = "urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0";

    private final static Logger logger = LoggerFactory.getLogger(DifiValidationController.class);

    private final SbdhValidator sbdhValidator;
    private final ValidatorFactory validatorFactory;

    @Autowired
    public DifiValidationController(@NotNull SbdhValidator sbdhValidator, @NotNull ValidatorFactory validatorFactory) {
        this.sbdhValidator = sbdhValidator;
        this.validatorFactory = validatorFactory;
        logger.info("Using DIFI validation controller");
    }

    @NotNull
    public ContainerMessage validate(@NotNull ContainerMessage containerMessage) {
        if (containerMessage.getDocumentInfo() == null) {
            throw new IllegalArgumentException("Document is null for " + containerMessage.getFileName());
        }

        Archetype archetype = containerMessage.getDocumentInfo().getArchetype();
        if (archetype == Archetype.INVALID || archetype == Archetype.UNRECOGNIZED) {
            return containerMessage;
        }
        String customizationId = containerMessage.getDocumentInfo().getCustomizationId();
        if (archetype == Archetype.EHF || archetype == Archetype.PEPPOL_BIS) {
            // detecting sub-types, like AT or PEPPOL_SI
            if (customizationId.contains("erechnung")) {
                archetype = Archetype.AT;
            } else if (customizationId.contains("simplerinvoicing")) {
                archetype = Archetype.PEPPOL_SI;
            }

        }
        return performValidation(containerMessage, archetype);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private ContainerMessage performValidation(@NotNull ContainerMessage cm, @NotNull Archetype archetype) {
        BasicValidator validator;
        try {
            validator = validatorFactory.getValidatorByArchetype(archetype);
        } catch (Exception e) {
            e.printStackTrace();
            String validatorFetchingError = e.getMessage();
            throw new IllegalArgumentException(
                    "No validator defined for archetype " + archetype + ", error: " + validatorFetchingError);
        }

        byte[] data;
        Document dom;
        try {
            dom = DocumentUtils.getDocument(cm);
            Document rootDocument = getRootDocument(cm, dom);
            data = DocumentContentUtils.getDocumentBytes(rootDocument);
        } catch (Exception e) {
            throw new IllegalArgumentException("Validation failed during XML transformation", e);
        }

        sbdhValidator.performXsdValidation(cm, dom);
        validator.validate(cm, data);

        if (!cm.getDocumentInfo().getErrors().isEmpty()) {
            cm.getDocumentInfo().setArchetype(Archetype.INVALID);
            cm.getDocumentInfo().getErrors().forEach(error -> logger.warn(error.toString()));
        }

        return cm;
    }

    @SuppressWarnings("ConstantConditions")
    private Document getRootDocument(@NotNull ContainerMessage containerMessage, @NotNull Document dom) throws Exception {
        Archetype at = containerMessage.getDocumentInfo().getArchetype();
        if (at == Archetype.INVALID || at == Archetype.UNRECOGNIZED) {
            throw new IllegalArgumentException("Unable to validate invalid documents: " + containerMessage.getFileName());
        }

        Node rootNode = DocumentUtils.getRootNode(dom);
        if (rootNode == null) {
            throw new IllegalArgumentException("No root node in the document");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();

        Node importedNode = newDocument.importNode(rootNode, true);
        newDocument.appendChild(importedNode);
        return newDocument;
    }

}
