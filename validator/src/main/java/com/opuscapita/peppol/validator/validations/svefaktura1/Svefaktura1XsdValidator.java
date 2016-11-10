package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.XsdValidator;
import com.opuscapita.peppol.commons.validation.util.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bambr on 16.3.10.
 */
@Component
public class Svefaktura1XsdValidator implements XsdValidator {
    @Value("${peppol.validator.svefaktura1.xsd.path}")
    String xsdPath;

    @Override
    public List<ValidationError> performXsdValidation(ContainerMessage containerMessage) {
        try {
            validateAgainstXsd(containerMessage, xsdPath);
        } catch (Exception e) {
            return new ArrayList<ValidationError>() {{
                add(ValidationErrorBuilder.aValidationError().withTitle("SBDH validation failure").withDetails(e.getMessage()).build());
            }};

        }
        return Collections.EMPTY_LIST;
    }

    public List<ValidationError> performXsdValidation(byte[] data) {
        try {
            validateAgainstXsd(data, xsdPath);
        } catch (Exception e) {
            return new ArrayList<ValidationError>() {{
                add(ValidationErrorBuilder.aValidationError().withTitle("SBDH validation failure").withDetails(e.getMessage()).build());
            }};

        }
        return Collections.EMPTY_LIST;
    }
}
