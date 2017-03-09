package com.opuscapita.peppol.support.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "logout", required = false) String logout){
        String test = logout;
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/error-login", method = RequestMethod.GET)
    public ModelAndView invalidLogin() {
        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("error", true);
        return modelAndView;
    }

    /*@RequestMapping(value = {"/logout"}, method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        String t = "Asdasd";
        System.out.println(request.toString());
        return null;
        //return new ModelAndView("login");
    }

    @RequestMapping(value = {"/logout"}, method = RequestMethod.POST)
    public ModelAndView logoutPost(HttpServletRequest request,HttpServletResponse response) {
        String t = "Asdasd";
        System.out.println(request.toString());
        return null;
        //return new ModelAndView("login");
    }*/
}
