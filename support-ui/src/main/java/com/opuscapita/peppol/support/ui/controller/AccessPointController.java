package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.accesspoint.AccessPoint;
import com.opuscapita.peppol.support.ui.accesspoint.AccessPointService;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by KACENAR1 on 22.12.2015
 */
@Controller
@RequestMapping("/rest/access_point")
public class AccessPointController {

    @Autowired
    private AccessPointService accessPointService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<AccessPoint> getAllAccessPoints(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return accessPointService.getAll(tableParameters);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getCount() {
        return "{\"length\": " + String.valueOf(accessPointService.getCount()) + "}";
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AccessPoint> updateCustomer(@RequestBody AccessPoint accessPoint) {
        accessPointService.update(accessPoint);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
