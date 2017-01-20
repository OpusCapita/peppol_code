package com.opuscapita.peppol.validator.web;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by bambr on 17.19.1.
 */
public class CustomizedErrorController implements ErrorController {
    private final static String PATH = "/error";

    @Override
    public String getErrorPath() {
        return PATH;
    }

    @RequestMapping(value = PATH)
    public ModelAndView error() {
        ModelAndView result = new ModelAndView("error");
        return result;
    }
}
