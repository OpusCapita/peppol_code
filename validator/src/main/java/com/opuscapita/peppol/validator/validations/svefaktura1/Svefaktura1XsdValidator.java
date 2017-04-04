package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.XsdValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bambr on 16.3.10.
 */
@Component
public class Svefaktura1XsdValidator implements XsdValidator {
    private final Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;

    @Autowired
    public Svefaktura1XsdValidator(@NotNull Svefaktura1ValidatorConfig svefaktura1ValidatorConfig) {
        this.svefaktura1ValidatorConfig = svefaktura1ValidatorConfig;
    }

    @Override
    public List<ValidationError> performXsdValidation(@NotNull ContainerMessage containerMessage, @NotNull Document dom) {
        try {
            validateAgainstXsd(containerMessage, svefaktura1ValidatorConfig.getSvefaktura1XsdPath());
        } catch (Exception e) {
            return new ArrayList<ValidationError>() {{
                add(new ValidationError("XSD validation failure").withTest("XSD validation").withText(e.getMessage()));
            }};

        }
        return Collections.emptyList();
    }

    List<ValidationError> performXsdValidation(byte[] data) {
        try {
            validateAgainstXsd(data, svefaktura1ValidatorConfig.getSvefaktura1XsdPath());
        } catch (Exception e) {
            return new ArrayList<ValidationError>() {{
                add(new ValidationError("XSD validation failure").withTest("XSD validation").withText(e.getMessage()));
            }};

        }
        return Collections.emptyList();
    }
}
