package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.domain.Customer;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.4.2
 * Time: 14:34
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/rest/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<Customer> getAllCustomer(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return customerService.getAll(tableParameters);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getAllCustomer() {
        return "{\"length\": " + String.valueOf(customerService.getCustomerCount()) + "}";
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer) {
        customerService.update(customer);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
