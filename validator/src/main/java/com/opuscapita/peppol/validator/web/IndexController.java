package com.opuscapita.peppol.validator.web;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.storage.Storage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.controller.ValidationController;
import com.opuscapita.peppol.validator.util.MultiPartHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Daniil on 03.05.2016.
 */
@Controller
public class IndexController {
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    private final ValidationController validationController;
    private final DocumentLoader documentLoader;
    private final ServerProperties serverProperties;
    private final Endpoint endpoint = new Endpoint("validator_web", ProcessType.WEB);
    private final Storage storage;
    private final Tracer tracer;

    @Autowired
    public IndexController(@NotNull ValidationController validationController, @NotNull DocumentLoader documentLoader,
                           @NotNull ServerProperties serverProperties, @NotNull Storage storage, Tracer tracer) {
        logger.debug("IndexController created.");
        this.validationController = validationController;
        this.documentLoader = documentLoader;
        this.serverProperties = serverProperties;
        this.storage = storage;
        this.tracer = tracer;
    }


    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView result = new ModelAndView("index");
        result.addObject("root", getServiceName(request));
        return result;
    }

    private String getServiceName(HttpServletRequest request) {
        String result = request.getHeader("Service") == null ? "/" : "/" + request.getHeader("Service") + "/";
        String contextPath = serverProperties.getContextPath();
        if (contextPath != null && contextPath.length() > 0 && "/".equals(result)) {
            result = "/" + contextPath + "/" + result; //Standalone deployment + context path set
        }
        result = result.replaceAll("//", "/");
        return result;
    }

    @SuppressWarnings("ConstantConditions")
    @PostMapping("/")
    public ModelAndView validate(@RequestParam(name = "datafile") MultipartFile dataFile, HttpServletRequest request) {
        Span span = tracer.createSpan("web validation");
        tracer.addTag("mode", "web");
        logger.debug("Got: " + dataFile.getOriginalFilename() + " as " + dataFile.getName() + " [" + dataFile.getSize() + "]");

        ModelAndView result = new ModelAndView("result");
        result.addObject("root", getServiceName(request));
        ContainerMessage containerMessage;
        try {
            containerMessage = MultiPartHelper.createContainerMessageFromMultipartFile(documentLoader, endpoint, storage, dataFile, "WEB", logger);//loadContainerMessageFromMultipartFile(dataFile);
            containerMessage = validationController.validate(containerMessage);
            ValidationResult validationResult = ValidationResult.fromContainerMessage(containerMessage);

            logger.debug("Validation passed for: " + dataFile.getOriginalFilename() + " -> " + validationResult.isPassed());
            logger.debug(containerMessage.getDocumentInfo().getProfileId());
            logger.debug(containerMessage.getDocumentInfo().getCustomizationId());
            validationResult.getErrors().forEach(error -> logger.debug(error.toString()));
            result.addObject("status", validationResult.isPassed());
            result.addObject("errors", validationResult.getErrors());
            result.addObject("warnings", containerMessage.getDocumentInfo().getWarnings());
        } catch (Exception e) {
            e.printStackTrace();
            ValidationError exceptionalError = new ValidationError().withFlag("FATAL").withTitle(e.getMessage()).withText(StringEscapeUtils.escapeHtml(e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            result.addObject("status", false);
            result.addObject("errors", new ArrayList<DocumentError>() {{
                add(exceptionalError.toDocumentError(endpoint));
            }});
            result.addObject("warnings", Collections.EMPTY_LIST);
        }
        tracer.close(span);
        return result;
    }
}
