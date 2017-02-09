package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.validation.BasicValidator;
import com.opuscapita.peppol.validator.validations.difi.DifiValidator;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.SveFaktura1Validator;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1XsdValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.6.10.
 */
@Component
public class ValidatorFactory {
    DifiValidator difiValidator;
    SveFaktura1Validator sveFaktura1Validator;
    private Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;
    private DifiValidatorConfig difiValidatorConfig;
    private Svefaktura1XsdValidator svefaktura1XsdValidator;

    public ValidatorFactory() {
    }


    @Autowired
    public ValidatorFactory(DifiValidatorConfig difiValidatorConfig, Svefaktura1ValidatorConfig svefaktura1ValidatorConfig, Svefaktura1XsdValidator svefaktura1XsdValidator) {
        this.difiValidatorConfig = difiValidatorConfig;
        this.svefaktura1ValidatorConfig = svefaktura1ValidatorConfig;
        this.svefaktura1XsdValidator = svefaktura1XsdValidator;
    }


    public synchronized BasicValidator getValidatorByArchetype(Archetype archetype) throws RuntimeException {
        BasicValidator result = null;
        try {
            switch (archetype) {
                case AT:
                case SI:
                case PEPPOL_BIS:
                case EHF:
                    if (difiValidator == null) {
                        difiValidator = new DifiValidator(difiValidatorConfig);
                    }
                    result = difiValidator;
                    break;
                case SVEFAKTURA1:
                    if(sveFaktura1Validator == null) {
                        sveFaktura1Validator = new SveFaktura1Validator(svefaktura1ValidatorConfig, svefaktura1XsdValidator);
                    }
                    result = sveFaktura1Validator;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


}
