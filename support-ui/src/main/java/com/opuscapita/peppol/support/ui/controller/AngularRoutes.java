package com.opuscapita.peppol.support.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by KACENAR1 on 2014.05.19..
 */
@Controller
public class AngularRoutes {
    @RequestMapping({
            "/customers",
            "/customer/{id}/*",
            "/status",
            "/outbound",
            "/outbound/*",
            "/preprocessing",
            "/preprocessing/*",
            "/access_point"
    })
    public String index() {
        return "forward:/";
    }
}
