package com.opuscapita.peppol.support.ui.controller;

import com.google.gson.Gson;
import com.itella.sp.usermanagement.Role;
import com.itella.sp.usermanagement.User;
import com.opuscapita.peppol.support.ui.service.CustomerService;
import com.opuscapita.peppol.support.ui.transport.TransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.io.*;

@SuppressWarnings("unused")
@Controller
public class ApplicationController {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Value("${peppol.email-notificator.status:''}")
    private String emailNotificatorStatusFile;

    @Autowired
    private CustomerService customerService;

    @ExceptionHandler
    public String handleException(NoSuchRequestHandlingMethodException ex) {
        return "forward:/";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            User user = (User) authentication.getPrincipal();
            map.addAttribute("user", user);
            map.addAttribute("customerListEnabled", isCustomerListEnabled());
            map.addAttribute("customerListMessage", isCustomerListEnabled() ? "" : "Customer e-mail sending disabled because of reasons");

            boolean admin = false;
            for (Role role : user.getRoles()) {
                if (role.getName().equals("admin") && role.getAdmin()) {
                    admin = true;
                }
            }
            return "index";
        }
        return "login";
    }

    @RequestMapping(value = "/smtp/{customerId}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getRecipientEmail(@PathVariable("customerId") String customerId) {
        return customerService.getCustomerEmail(customerId);
    }

    @RequestMapping(value = "/smtp/{customerId}/{direction}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getCustomerEmailByDirection(@PathVariable("customerId") String customerId,
                                       @PathVariable("direction") TransportType direction) {
        return customerService.getCustomerEmail(customerId, direction);
    }

    private boolean isCustomerListEnabled() {
        if (emailNotificatorStatusFile != null && emailNotificatorStatusFile.trim().length() > 0) {
            if (new File(emailNotificatorStatusFile).exists()) {
                try (Reader reader = new FileReader(emailNotificatorStatusFile)) {
                    EmailNotificatorStatus emailNotificatorStatus = new Gson().fromJson(reader, EmailNotificatorStatus.class);
                    return emailNotificatorStatus.isInboundCustomerEmailEnabled() || emailNotificatorStatus.isOutboundCustomerEmailEnabled();
                } catch (Exception e) {
                    logger.error("Failed to read email-notificator status from " + emailNotificatorStatusFile, e);
                }
            }
        }
        return true;
    }
}
