package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.service.FileInfoService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by KACENAR1 on 01.10.2014
 */

@Controller
@RequestMapping("/rest/reprocess")
public class ReprocessController {
    public static final Logger logger = Logger.getLogger(ReprocessController.class);

    @Autowired
    private FileInfoService fileInfoService;

    @RequestMapping(value = "/inbound/{fileIds}", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    void reprocessInboundMessages(@PathVariable Integer... fileIds) throws Exception {
        for (Integer fileId : fileIds) {
            try {
                fileInfoService.reprocessFile(fileId, false);
            }
            catch (Exception e) {
                logger.warn("Unable to find file: " + fileId);
            }
        }
    }

    @RequestMapping(value = "/outbound/{fileIds}", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    void reprocessOutboundMessages(@PathVariable Integer... fileIds) throws IOException {
        for (Integer fileId : fileIds) {
            try {
                fileInfoService.reprocessFile(fileId, true);
            } catch (Exception e) {
                logger.warn("Unable to find file: " + fileId);
            }
        }
    }
}
