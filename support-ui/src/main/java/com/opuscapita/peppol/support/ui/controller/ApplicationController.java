package com.opuscapita.peppol.support.ui.controller;

import com.google.gson.Gson;
import com.itella.sp.usermanagement.Role;
import com.itella.sp.usermanagement.User;
import com.opuscapita.peppol.support.ui.service.CustomerService;
import com.opuscapita.peppol.support.ui.transport.TransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

@SuppressWarnings("unused")
@Controller
public class ApplicationController {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    // @Value("${peppol.email-notificator.status:/peppol/interop/email-notificator.json}")
    private final static String emailNotificatorStatusFile = "/peppol/interop/email-notificator.json";

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
            map.addAttribute("customerListMessage", isCustomerListEnabled() ? "" :
                    "Dear user,<br/>" +
                            "The communication logic of outbound related error messages has changed.<br/>" +
                            "Instead of sending direct email to invoice sender our Access Point informs business platform (XIB, A2A or Sirius)" +
                            " about negative acknowledgment (MLR). Customer communication is now executed by A2A gateways and" +
                            " XIB WWD like in case of any other negative acknowledgment.<br/>" +
                            "Displayed email addresses in this webpage were synced with business platforms before the change." +
                            " This email list shouldn’t be updated anymore and customer contacts are now maintained in respective" +
                            " business platform.<br/>" +
                            "<br/>" +
                            "NB! Inbound related error message handling remains currently the same." +
                            " We have a plan to change that as well – error messages will be delivered to sending access point" +
                            " instead of customer (we will notify you about the upcoming change).");

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
            } else {
                logger.info("Email-notificator status file " + emailNotificatorStatusFile + " not found, skipping");
            }
        }
        return true;
    }
}
