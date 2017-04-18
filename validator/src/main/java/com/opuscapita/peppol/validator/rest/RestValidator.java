package com.opuscapita.peppol.validator.rest;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
    private final Storage storage;
    private final Endpoint endpoint = new Endpoint("validator_rest", ProcessType.REST);

    @Autowired
    public RestValidator(@NotNull ValidationController validationController, @NotNull DocumentLoader documentLoader,
                         @NotNull Storage storage) {
        this.validationController = validationController;
        this.documentLoader = documentLoader;
        this.storage = storage;
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ValidationResult validate(@RequestParam("file") MultipartFile file, @RequestParam(name = "test", required = false, defaultValue = "false") boolean useTestArtifacts) {
        if (StringUtils.isNotBlank(file.getOriginalFilename())) {
            logger.info("About to validate custom file: " + file.getOriginalFilename());
        }

        ValidationResult result = new ValidationResult();
        ContainerMessage containerMessage;
        try {
            String tempFilePath = storage.storeTemporary(file.getInputStream(), file.getName());
            logger.info("Validating file received via REST call and stored as " + tempFilePath);

            containerMessage = new ContainerMessage("REST /validate", tempFilePath, endpoint);
            containerMessage.setDocumentInfo(documentLoader.load(tempFilePath, endpoint));
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
