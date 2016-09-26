package com.opuscapita.peppol.support.ui.controller;

import com.opuscapita.peppol.support.ui.common.Util;
import com.opuscapita.peppol.support.ui.domain.FileInfo;
import com.opuscapita.peppol.support.ui.domain.Message;
import com.opuscapita.peppol.support.ui.domain.TableParameters;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.opuscapita.peppol.support.ui.service.FileInfoService;
import com.opuscapita.peppol.support.ui.service.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.4.2
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/rest/outbound")
public class OutboundMessageController {
    private static final Logger logger = Logger.getLogger(OutboundMessageController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private Util util;

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getAllMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getAllMessages(tableParameters);
        //return messageService.getMessageList(tableParameters);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getAllMessageCount() {
        return "{\"length\": " + String.valueOf(messageService.getAllMessageCount()) + "}";
    }

    @RequestMapping(value = "/failed", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getFailedMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getFailedMessages(tableParameters);
    }

    @RequestMapping(value = "/failed/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getFailedMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getFailedMessageCount()) + "}";
    }

    @RequestMapping(value = "/failed/resolve_manually/{messageId}", method = RequestMethod.POST)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    void resovleMessage(@PathVariable Integer messageId, @RequestParam String comment) {
        messageService.resolveManually(messageId, comment);
    }

    @RequestMapping(value = "/invalid", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getInvalidMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getInvalidMessages(tableParameters);
    }

    @RequestMapping(value = "/invalid/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getInvalidMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getInvalidMessageCount()) + "}";
    }

    @RequestMapping(value = "/sent", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getSentMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getSentMessages(tableParameters);
    }

    @RequestMapping(value = "/sent/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getSentMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getSentMessageCount()) + "}";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    Message getMessageById(@PathVariable Integer id) {
        return messageService.getById(id);
    }

    @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getCustomerMessages(@PathVariable Integer customerId, @RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getCustomerMessages(customerId, tableParameters);
    }

    @RequestMapping(value = "/customer/{customerId}/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getCustomerMessageCount(@PathVariable Integer customerId) {
        return "{\"length\": " + String.valueOf(messageService.getCustomerMessageCount(customerId)) + "}";
    }

    @RequestMapping(value = "/get_error", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    ErrorMessage getErrorMessage(@RequestParam("file") String fileName) {
        String error = messageService.getErrorMessage(fileName);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setFullError(error);
        return errorMessage;
    }

    @RequestMapping(value = "/download/{fileName:.*}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public void downloadMessage(@PathVariable String fileName, HttpServletResponse response) {
        try {
            final byte[] data = util.findMessage(fileName);
            response.setHeader("Content-Length", String.valueOf(data.length));
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType("application/xml");
            response.getOutputStream().write(data);
            response.flushBuffer();
        } catch (IOException e1) {
            logger.warn(e1.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/details/{messageId}", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<FileInfo> getMessageDetails(@PathVariable Integer messageId) {
        return fileInfoService.getMessageFileInfos(messageId);
    }

    @RequestMapping(value = "/processing", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getProcessingMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getProcessingMessages(tableParameters);
    }

    @RequestMapping(value = "/processing/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getProcessingMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getProcessingMessageCount()) + "}";
    }

    @RequestMapping(value = "/reprocessed", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    List<MessageDTO> getReprocessedMessages(@RequestParam Map<String, String> requestParams) {
        TableParameters tableParameters = new TableParameters(requestParams);
        return messageService.getReprocessedMessages(tableParameters);
    }

    @RequestMapping(value = "/reprocessed/count", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public
    @ResponseBody
    String getReprocessedMessageCount() {
        return "{\"length\":" + String.valueOf(messageService.getReprocessedMessageCount()) + "}";
    }

    static class ErrorMessage {
        private String fullError;

        public String getFullError() {
            return this.fullError;
        }

        public void setFullError(String fullError) {
            this.fullError = fullError;
        }
    }

}
