package com.opuscapita.peppol.validator.rest;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.validator.validations.ValidationController;
import com.opuscapita.peppol.validator.validations.common.ValidationResult;
import com.opuscapita.peppol.validator.validations.common.util.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Daniil on 03.05.2016.
 */
@RestController("/rest")
public class RestValidator {
    @Autowired
    ValidationController validationController;

    @Autowired
    DocumentLoader documentLoader;

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    public ValidationResult validate(@RequestParam("file") MultipartFile file, @RequestParam(name = "test", required = false, defaultValue = "false") boolean useTestArtifacts) {
        ValidationResult validationResult;
        ContainerMessage containerMessage;
        try {
            containerMessage = new ContainerMessage("REST /validate", file.getName(), Endpoint.REST)
                    .setBaseDocument(documentLoader.load(file.getInputStream(), file.getName()));
            validationResult = validationController.validate(containerMessage);

        } catch (IOException e) {
            e.printStackTrace();
            validationResult = new ValidationResult(Archetype.INVALID);
            validationResult.addError(ValidationErrorBuilder.aValidationError().withTitle("I/O failure when validating via ReST")
                    .withDetails(e.getMessage()).build());
        }


        return validationResult;
    }
}
