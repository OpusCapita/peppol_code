package com.opuscapita.peppol.validator.web;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentLoader;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationResult;
import com.opuscapita.peppol.validator.validations.ValidationController;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Daniil on 03.05.2016.
 */
@Controller
public class IndexController {
    @Autowired
    ValidationController validationController;

    @Autowired
    ServletContext servletContext;

    @Autowired
    DocumentLoader documentLoader;


    public IndexController() {
        System.out.println("IndexController created.");
    }


    @GetMapping("/")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView result = new ModelAndView("index");
        result.addObject("root", "/" + request.getHeader("Service") + "/");
        return result;
    }

    @PostMapping("/")
    public ModelAndView validate(@RequestParam(name = "datafile") MultipartFile dataFile, HttpServletRequest request) {
        System.out.println("Got: " + dataFile.getOriginalFilename() + " as " + dataFile.getName() + " [" + dataFile.getSize() + "]");

        ModelAndView result = new ModelAndView("result");
        result.addObject("root", "/" + request.getHeader("Service") + "/");
        try {
            ContainerMessage containerMessage = loadContainerMessageFromMultipartFile(dataFile);
            ValidationResult validationResult = validationController.validate(containerMessage);
            System.out.println("Validation passed for: " + dataFile.getOriginalFilename() + " -> " + validationResult.isPassed());
            validationResult.getErrors().forEach(error -> System.out.println(error.toString()));
            result.addObject("status", validationResult.isPassed());
            result.addObject("errors", validationResult.getErrors());
        } catch (IOException e) {
            e.printStackTrace();
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
