package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import com.opuscapita.peppol.validator.validations.difi.DifiValidator;
import com.opuscapita.peppol.validator.validations.svefaktura1.SveFaktura1Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;

/**
 * Created by bambr on 16.5.8.
 */
@Component
public class ValidationController {

    @Autowired
    ApplicationContext context;

    public ValidationResult validate(ContainerMessage containerMessage) {
        Archetype archetype = containerMessage.getDocument().getArchetype();
        BasicValidator validator = getValidator(archetype);
        ValidationResult result = new ValidationResult(containerMessage.getDocument().getArchetype());
        result.setPassed(false);
        if (validator != null) {
            byte[] data = new byte[0];
            try {
                data = getDocumentBytes(containerMessage);
            } catch (TransformerException e) {
                e.printStackTrace();
                addNonTypicalErrorToValidationResult(result, "Validation failed on transforming XML to byte array", e.getMessage());
            }
            result.setPassed(validator.validate(data));
            if (!result.isPassed()) {
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
                validator = context.getBean("svefaktura1", SveFaktura1Validator.class);
                break;
            case INVALID:

                break;
            case UBL:
            default:
                validator = context.getBean("difi", DifiValidator.class);
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

    private byte[] getDocumentBytes(ContainerMessage containerMessage) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(containerMessage.getDocument().getDocument());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(bos);
        transformer.transform(source, result);
        return bos.toByteArray();
    }
}
