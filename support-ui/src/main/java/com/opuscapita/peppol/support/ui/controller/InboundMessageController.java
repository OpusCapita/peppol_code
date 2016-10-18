package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.opuscapita.peppol.support.ui.service.FileInfoService;
import com.opuscapita.peppol.support.ui.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by KACENAR1 on 21.06.2014
 */
@Controller
@RequestMapping("/rest/inbound")
public class InboundMessageController {

    @Autowired
    private MessageService messageService;
    @Autowired
    private FileInfoService fileInfoService;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getAllMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getAllInboundMessages(tableParameters);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getAllMessageCount() {
        return "{\"length\": " + String.valueOf(messageService.getAllInboundMessageCount()) + "}";
    }

    @RequestMapping(value = "/invalid", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getInvalidMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getInvalidInboundMessages(tableParameters);
    }

    @RequestMapping(value = "/invalid/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getInvalidMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getInvalidInboundMessageCount()) + "}";
    }

    @RequestMapping(value = "/details/{messageId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<FileInfo> getMessageDetails(@PathVariable Integer messageId) {
        return fileInfoService.getMessageFileInfos(messageId);
    }
}
