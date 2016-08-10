package com.opuscapita.peppol.validator.validations;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.validator.validations.common.BasicValidator;
import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import com.opuscapita.peppol.validator.validations.difi.DifiValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by bambr on 16.5.8.
 */
@Component
public class ValidationController {

    @Autowired
    ApplicationContext context;

    public ValidationResult validate(ContainerMessage containerMessage) {
        String profileId = containerMessage.getDocument().getProfileId();
        String archetype = extractArchetype(profileId);
        BasicValidator validator;
        switch (archetype) {
            case "SVEFAKTURA1":

                break;
            default:
                validator = context.getBean("difi", DifiValidator.class);
        }
        //validator.validate(containerMessage) //get byte[] out of Document type
        return null;
    }

    private String extractArchetype(String profileId) {
        if(profileId.toLowerCase().contains("urn:sfti:documents:BasicInvoice:1:0")) {
            return "SVEFAKTURA1";
        }
        return null;
    }
}
