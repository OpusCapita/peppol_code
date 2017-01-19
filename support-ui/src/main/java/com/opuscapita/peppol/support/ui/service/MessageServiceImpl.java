package com.opuscapita.peppol.support.ui.service;

import com.opuscapita.peppol.support.ui.common.Util;
import com.opuscapita.peppol.support.ui.dao.MessageDAO;
import com.opuscapita.peppol.support.ui.domain.*;
import com.opuscapita.peppol.support.ui.dto.MessageDTO;
import com.opuscapita.peppol.support.ui.dto.PeppolEvent;
import com.opuscapita.peppol.support.ui.transport.TransportType;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 13.4.12
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
@Service
public class MessageServiceImpl implements MessageService {
    private static final Logger logger = Logger.getLogger(MessageService.class);

    @Autowired
    private MessageDAO messageDAO;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private Util util;

    @Override
    public void add(Message message) {
        messageDAO.add(message);
    }

    @Override
    public Message createMessage(PeppolEvent peppolEvent) {

        Customer customer = customerService.getByCustomerId(peppolEvent.getSenderId());
        if (customer == null) {
            customerService.add(peppolEvent);
            customer = customerService.getByCustomerId(peppolEvent.getSenderId());
        }
        Message message = new Message();
        message.setSender(customer);
        message.setRecipientId(peppolEvent.getRecipientId());
        message.setInvoiceNumber(peppolEvent.getInvoiceId());
        message.setDocumentType(peppolEvent.getDocumentType());
        message.setStatus(MessageStatus.processing);

        if (peppolEvent.getTransportType() == TransportType.IN_IN || peppolEvent.getTransportType() == TransportType.IN_OUT) {
            message.setDirection(Direction.IN);
        } else {
            message.setDirection(Direction.OUT);
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date date = sdf1.parse(peppolEvent.getInvoiceDate());
            message.setInvoiceDate(new Date(date.getTime()));
        } catch (ParseException e) {
            logger.warn("Failed to parse Invoice Date: " + peppolEvent.getInvoiceDate());
        }

        // Not all messages have Due Date
        try {
            java.util.Date dueDate = sdf1.parse(peppolEvent.getDueDate());
            message.setDueDate(new Date(dueDate.getTime()));
        } catch (Exception pass) {
            logger.debug("Document has no due date.");
        }
        return message;
    }

    @Deprecated
    @Override
    public Message createMessage(PeppolEvent peppolEvent, FileInfo fileInfo) {
        Customer customer = customerService.getByCustomerId(peppolEvent.getSenderId());
        if (customer == null) {
            customerService.add(peppolEvent);
            customer = customerService.getByCustomerId(peppolEvent.getSenderId());
        }
        return createMessage(customer, peppolEvent, fileInfo);
    }

    @Deprecated
    public Message createMessage(Customer customer, PeppolEvent peppolEvent, FileInfo fileInfo) {
        Message message = new Message();
        message.setFiles(new TreeSet<FileInfo>(Arrays.asList(fileInfo)));
        message.setSender(customer);
        message.setRecipientId(peppolEvent.getRecipientId());
        message.setInvoiceNumber(peppolEvent.getInvoiceId());

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date date = sdf1.parse(peppolEvent.getInvoiceDate());
            message.setInvoiceDate(new Date(date.getTime()));
        } catch (ParseException e) {
            logger.warn("Failed to parse Invoice Date: " + peppolEvent.getInvoiceDate());
        }

        // Not all messages have Due Date
        try {
            java.util.Date dueDate = sdf1.parse(peppolEvent.getDueDate());
            message.setDueDate(new Date(dueDate.getTime()));
        } catch (Exception pass) {
            logger.debug("Document has no due date.");
        }
        return message;
    }

    @Override
    public List<Message> getAll() {
        List<Message> list = messageDAO.getAll();
        return list;
    }

    @Override
    public Message getById(Integer id) {
        return messageDAO.getById(id);
    }

    @Override
    public void update(Message message) {
        messageDAO.update(message);
    }

    @Override
    public void delete(Message message) {
        messageDAO.delete(message);
    }

    @Override
    public void delete(Integer id) {
        messageDAO.delete(id);
    }

    @Override
    public Message getByPeppolEvent(PeppolEvent peppolEvent) {
        try {
            Customer customer = customerService.getByCustomerId(peppolEvent.getSenderId());
            if (customer == null) {
                customerService.add(peppolEvent);
                customer = customerService.getByCustomerId(peppolEvent.getSenderId());
            }
            Message message = messageDAO.getByDocument(customer, peppolEvent.getInvoiceId());
            return message;
        } catch (Exception e) {
            logger.error("Failed to get customer using document: " + e.getMessage());
        }
        return null;
    }

    private List<MessageDTO> getMessages(TableParameters tableParameters, String fetchScope) throws HibernateException {
        if (fetchScope.equals("all")) {
            return messageDAO.getAllOutboundMessages(tableParameters);
        } else if (fetchScope.equals("invalid")) {
            return messageDAO.getInvalidMessages(tableParameters);
        } else if (fetchScope.equals("failed")) {
            return messageDAO.getFailedMessages(tableParameters);
        } else if (fetchScope.equals("sent")) {
            return messageDAO.getSentMessages(tableParameters);
        } else if (fetchScope.equals("processing")) {
            return messageDAO.getProcessingMessages(tableParameters);
        } else if (fetchScope.equals("reprocessed")) {
            return messageDAO.getReprocessedMessages(tableParameters);
        }
        return null;
    }


    @Override
    public List<MessageDTO> getAllMessages(TableParameters tableParameters) throws HibernateException {
        return getMessages(tableParameters, "all");
    }

    @Override
    public List<MessageDTO> getInvalidMessages(TableParameters tableParameters) throws HibernateException {
        return getMessages(tableParameters, "invalid");
    }

    @Override
    public List<MessageDTO> getFailedMessages(TableParameters tableParameters) throws HibernateException {
        List<MessageDTO> messages = getMessages(tableParameters, "failed");
        return messages;
    }

    @Override
    public List<MessageDTO> getProcessingMessages(TableParameters tableParameters) throws HibernateException {
        return getMessages(tableParameters, "processing");
    }

    @Override
    public List<MessageDTO> getReprocessedMessages(TableParameters tableParameters) throws HibernateException {
        return getMessages(tableParameters, "reprocessed");
    }

    @Override
    public List<MessageDTO> getSentMessages(TableParameters tableParameters) throws HibernateException {
        return getMessages(tableParameters, "sent");
    }

    @Override
    @Cacheable("allMessages")
    public int getAllMessageCount() throws HibernateException {
        return messageDAO.getAllMessageCount();
    }

    @Override
    @Cacheable("invalidMessages")
    public int getInvalidMessageCount() throws HibernateException {
        return messageDAO.getInvalidMessageCount();
    }

    @Override
    @Cacheable("failedMessages")
    public int getFailedMessageCount() throws HibernateException {
        return messageDAO.getFailedMessageCount();
    }

    @Override
    @Cacheable("sentMessages")
    public int getSentMessageCount() throws HibernateException {
        return messageDAO.getSentMessageCount();
    }

    @Override
    @Cacheable("reprocessedMessages")
    public int getReprocessedMessageCount() throws HibernateException {
        return messageDAO.getReprocessedMessageCount();
    }

    @Override
    @Cacheable("processingMessages")
    public int getProcessingMessageCount() throws HibernateException {
        return messageDAO.getProcessingMessageCount();
    }

    @Override
    public List<MessageDTO> getInvalidInboundMessages(TableParameters tableParameters) throws HibernateException {
        return messageDAO.getInvalidInboundMessages(tableParameters);
    }

    @Override
    public List<MessageDTO> getAllInboundMessages(TableParameters tableParameters) throws HibernateException {
        return messageDAO.getAllInboundMessages(tableParameters);
    }

    @Override
    @Cacheable("invalidInboundMessages")
    public int getInvalidInboundMessageCount() throws HibernateException {
        return messageDAO.getInvalidInboundMessageCount();
    }

    @Override
    @Cacheable("allInboundMessages")
    public int getAllInboundMessageCount() throws HibernateException {
        return messageDAO.getAllInboundMessageCount();
    }

    @Override
    public String getErrorMessage(String filePath) {
        String result = null;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                result = new String(util.findMessage(FilenameUtils.getName(filePath)), "UTF-8");
            } catch (Exception e) {
                logger.warn("Unable to find file: " + filePath);
                return "";
            }
        }
        if (result != null) {
            return result;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            result = IOUtils.toString(fis, "UTF-8");
        } catch (IOException e) {
            logger.warn("Unable to read file " + filePath + ": " + e.getMessage());
        }
        return result;
    }

    @Override
    public List<MessageDTO> getCustomerMessages(Integer customerId, TableParameters tableParameters) {
        return messageDAO.getCustomerMessages(customerId, tableParameters);
    }


    @Override
    public int getCustomerMessageCount(Integer customerId) {
        Customer customer = customerService.getById(customerId);
        return getCustomerMessageCount(customer);
    }

    @Override
    public int getCustomerMessageCount(Customer customer) {
        return messageDAO.getCustomerMessageCount(customer);
    }

    @Override
    public void resolveManually(Integer messageId, String comment) {
        if (comment.length() > 1000) {
            comment = comment.substring(0, 999);
        }
        messageDAO.resolveManually(messageId, comment);
    }

    @Override
    @Cacheable("allOutboundMessages")
    public int getAllOutboundMessageCount() throws HibernateException{
        return messageDAO.getAllOutboundMessageCount();
    }

}
