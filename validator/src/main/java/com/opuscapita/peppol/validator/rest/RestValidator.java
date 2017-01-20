package com.opuscapita.peppol.validator.rest;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.document.impl.Archetype;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Created by Daniil on 03.05.2016.
 */
@RestController("/rest")
public class RestValidator {
    private final ValidationController validationController;
    private final DocumentLoader documentLoader;

    private final static Logger logger = LoggerFactory.getLogger(RestValidator.class);

    @Autowired
    public RestValidator(ValidationController validationController, DocumentLoader documentLoader) {
        this.validationController = validationController;
        this.documentLoader = documentLoader;
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ValidationResult validate(@RequestParam("file") MultipartFile file, @RequestParam(name = "test", required = false, defaultValue = "false") boolean useTestArtifacts) {
        logger.info("About to validate file: " + file.getOriginalFilename());
        ValidationResult validationResult;
        ContainerMessage containerMessage;
        try {
            containerMessage = new ContainerMessage("REST /validate", file.getName(), new Endpoint("validator_rest", Endpoint.Type.REST))
                    .setBaseDocument(documentLoader.load(file.getInputStream(), file.getName()));
            validationResult = validationController.validate(containerMessage);
            logger.info("Validation performed normally with result: " + validationResult.isPassed());
        } catch (IOException e) {
            logger.error("Validation failed with error: " + e.getMessage());
            e.printStackTrace();
            validationResult = new ValidationResult(Archetype.INVALID);
            validationResult.addError(new ValidationError("I/O failure when validating via ReST").withText(e.getMessage()));
        }


        return validationResult;
    }
}
