package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bambr on 16.3.10.
 */
@Component
public class SbdhValidator implements XsdValidator {
    @Value("${peppol.validator.sbdh.xsd}")
    String xsdPath;

    @Override
    public List<ValidationError> performXsdValidation(ContainerMessage containerMessage) {
        try {
            validateAgainstXsd(containerMessage, xsdPath);
        } catch (Exception e) {
            if (!e.getMessage().contains("cvc-elt.1: Cannot find the declaration of element 'Invoice'")) {
                return new ArrayList<ValidationError>() {{
                    add(ValidationErrorBuilder.aValidationError().withTitle("SBDH validation failure").withDetails(e.getMessage()).build());
                }};
            }
        }
        return Collections.EMPTY_LIST;
    }


}
