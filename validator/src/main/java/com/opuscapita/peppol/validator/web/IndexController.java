package com.opuscapita.peppol.validator.web;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Daniil on 03.05.2016.
 */
@Controller
public class IndexController {
    @RequestMapping("/")
    public ModelAndView index(HttpRequest request) {
        ModelAndView result = new ModelAndView("index");

        return result;
    }
}
