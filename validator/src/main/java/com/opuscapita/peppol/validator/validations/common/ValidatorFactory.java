package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.validator.validations.difi.DifiValidator;
import com.opuscapita.peppol.validator.validations.difi.DifiValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.SveFaktura1Validator;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1ValidatorConfig;
import com.opuscapita.peppol.validator.validations.svefaktura1.Svefaktura1XsdValidator;
import no.difi.vefa.validator.api.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by bambr on 16.6.10.
 */
@Component
public class ValidatorFactory {
    @Autowired
    Svefaktura1ValidatorConfig svefaktura1ValidatorConfig;

    @Autowired
    DifiValidatorConfig difiValidatorConfig;

    @Autowired
    DifiValidatorConfig simplerInvoicingValidatorConfig;

    @Autowired
    DifiValidatorConfig austrianValidatorConfig;

    @Autowired
    Svefaktura1XsdValidator svefaktura1XsdValidator;

    Map<Archetype, BasicValidator> validatorCache = new ConcurrentHashMap<>();

    public BasicValidator getValidatorByArchetype(Archetype archetype) throws RuntimeException {
        return validatorCache.computeIfAbsent(archetype, (at) -> {
            BasicValidator result = null;
            try {
                switch (archetype) {
                    case UBL:
                        result = new DifiValidator(difiValidatorConfig);
                    case SVEFAKTURA1:
                        result = new SveFaktura1Validator(svefaktura1ValidatorConfig, svefaktura1XsdValidator);
                        break;
                    case AT:
                        result = new DifiValidator(austrianValidatorConfig);
                        break;
                    case SI:
                        result = new DifiValidator(simplerInvoicingValidatorConfig);
                        break;
                    case INVALID:
                        result = null;
                        break;
                }
            } catch (ValidatorException e) {
                throw new RuntimeException(e);
            }
            return result;
        });
    }
}
