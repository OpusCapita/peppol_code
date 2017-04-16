package com.opuscapita.peppol.validator.rest;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
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
    private final static Logger logger = LoggerFactory.getLogger(RestValidator.class);
    private final ValidationController validationController;
    private final DocumentLoader documentLoader;

    @Autowired
    public RestValidator(ValidationController validationController, DocumentLoader documentLoader) {
        this.validationController = validationController;
        this.documentLoader = documentLoader;
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ValidationResult validate(@RequestParam("file") MultipartFile file, @RequestParam(name = "test", required = false, defaultValue = "false") boolean useTestArtifacts) {
        logger.info("About to validate file: " + file.getOriginalFilename());
        ValidationResult result = new ValidationResult();
        Endpoint endpoint = new Endpoint("validator_rest", ProcessType.REST);
        ContainerMessage containerMessage;
        try {
            containerMessage = new ContainerMessage("REST /validate", file.getName(), endpoint);
            containerMessage.setDocumentInfo(documentLoader.load(file.getInputStream(), file.getName(), endpoint));
            containerMessage = validationController.validate(containerMessage);
            result = ValidationResult.fromContainerMessage(containerMessage);

            logger.info("Validation performed normally with result: " + result.isPassed());
        } catch (IOException e) {
            logger.error("Validation failed with error: " + e.getMessage(), e);
            result.addError(new DocumentError(endpoint, "I/O failure when validating via ReST: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Validation technical failure: " + e.getMessage(), e);
            result.addError(new DocumentError(endpoint, "Validation technical failure: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Validation failed with error: " + e.getMessage());
            result.addError(new DocumentError(endpoint, "Validator error: " + e.getMessage()));
        }

        return result;
    }
}
