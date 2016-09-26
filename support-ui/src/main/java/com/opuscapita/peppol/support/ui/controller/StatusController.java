package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.domain.AmqpStatus;
import com.opuscapita.peppol.support.ui.dto.QuartzStatusDTO;
import com.opuscapita.peppol.support.ui.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by KACENAR1 on 14.24.2.
 */
@Controller
@RequestMapping("/rest/status")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @RequestMapping(value = "/amqp", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<AmqpStatus> getAllMessageCount() {
        return statusService.getAmqpStatuses();
    }

    @RequestMapping(value = "/quartz", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    QuartzStatusDTO getQuartzStatusAndPID() {
        QuartzStatusDTO quartzStatusDTO = new QuartzStatusDTO();
        quartzStatusDTO.setPid(statusService.getOutboundProcessId());
        quartzStatusDTO.setStatus(statusService.getQuartzStatus());
        return quartzStatusDTO;
    }
}
