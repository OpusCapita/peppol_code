package com.opuscapita.peppol.validator.validations.svefaktura1;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.XsdValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

/**
 * Created by bambr on 16.3.10.
 */
//@Component
public class Svefaktura1XsdValidator implements XsdValidator {
    private final Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;

    @Autowired
    public Svefaktura1XsdValidator(@NotNull Svefaktura1ValidatorConfig svefaktura1ValidatorConfig) {
        this.svefaktura1ValidatorConfig = svefaktura1ValidatorConfig;
    }

    @Override
    public void performXsdValidation(@NotNull ContainerMessage cm, @NotNull Document dom) {
        try {
            validateAgainstXsd(cm, svefaktura1ValidatorConfig.getSvefaktura1XsdPath());
        } catch (Exception e) {
            cm.addError("XSD validation failure: " + e.getMessage());
        }
    }

    void performXsdValidation(@NotNull ContainerMessage cm, @NotNull byte[] data) {
        try {
            validateAgainstXsd(data, svefaktura1ValidatorConfig.getSvefaktura1XsdPath());
        } catch (Exception e) {
            cm.addError("XSD validation failure: " + e.getMessage());
        }
    }
}
