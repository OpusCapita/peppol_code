package com.opuscapita.peppol.validator.web;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Daniil on 03.05.2016.
 */
@Controller
public class IndexController {
    @Autowired
    ValidationController validationController;

    @Autowired
    DocumentLoader documentLoader;

    @Autowired
    ServerProperties serverProperties;


    public IndexController() {
        System.out.println("IndexController created.");
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

    @PostMapping("/")
    public ModelAndView validate(@RequestParam(name = "datafile") MultipartFile dataFile, HttpServletRequest request) {
        System.out.println("Got: " + dataFile.getOriginalFilename() + " as " + dataFile.getName() + " [" + dataFile.getSize() + "]");

        ModelAndView result = new ModelAndView("result");
        result.addObject("root", getServiceName(request));
        ContainerMessage containerMessage = null;
        try {
            containerMessage = loadContainerMessageFromMultipartFile(dataFile);
            ValidationResult validationResult = validationController.validate(containerMessage);
            System.out.println("Validation passed for: " + dataFile.getOriginalFilename() + " -> " + validationResult.isPassed());
            System.out.println(containerMessage.getBaseDocument().getProfileId());
            System.out.println(containerMessage.getBaseDocument().getCustomizationId());
            validationResult.getErrors().forEach(error -> System.out.println(error.toString()));
            result.addObject("status", validationResult.isPassed());
            result.addObject("errors", validationResult.getErrors());
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            ValidationError exceptionalError = new ValidationError().withFlag("FATAL").withTitle(e.getMessage()).withText(StringEscapeUtils.escapeHtml(e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
            result.addObject("status", false);
            result.addObject("errors", new ArrayList<ValidationError>() {{
                add(exceptionalError);
            }});
        }
        return result;
    }

    @NotNull
    private ContainerMessage loadContainerMessageFromMultipartFile(MultipartFile dataFile) throws IOException {
        return new ContainerMessage(
                dataFile.getName(), dataFile.getName(), new Endpoint("validator_rest", Endpoint.Type.REST))
                .setBaseDocument(documentLoader.load(dataFile.getInputStream(), dataFile.getName()));
    }
}
