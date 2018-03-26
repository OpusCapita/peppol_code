package com.opuscapita.peppol.events.persistence.controller;

import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.commons.model.FileInfo;
import com.opuscapita.peppol.commons.model.Message;
import com.opuscapita.peppol.events.persistence.model.CustomerRepository;
import com.opuscapita.peppol.events.persistence.model.FileInfoRepository;
import com.opuscapita.peppol.events.persistence.model.MessageRepository;
import org.apache.commons.collections4.map.LRUMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class DataLoadService {
    private final static Logger logger = LoggerFactory.getLogger(DataLoadService.class);
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final FileInfoRepository fileInfoRepository;
    //LRUMap<String, Message> messageCache = new LRUMap<>(10);
    LRUMap<String, Customer> customerCache = new LRUMap<>(10);
    //LRUMap<String, FileInfo> fileInfoCache = new LRUMap<>(10);

    @Value("${peppol.events-persistence.cache.enabled:false}")
    Boolean chacheEnabled;


    @Autowired
    public DataLoadService(MessageRepository messageRepository, CustomerRepository customerRepository, FileInfoRepository fileInfoRepository) {
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.fileInfoRepository = fileInfoRepository;
    }

    public Message fetchMessageFromDb(String invoiceId, Integer customerId) {
        String key = invoiceId + customerId;
      /*  if (messageCache.containsKey(key)) {
            logger.debug("Cache hit for " + key);
            return messageCache.get(key);
        } else {
            logger.debug("Cache miss for " + key);*/
        Message message = messageRepository.fetchBySenderIdAndInvoiceNumber(customerId, invoiceId);//messageRepository.findBySenderAndInvoiceNumber(customer, invoiceId);
        /*            messageCache.put(key, message);*/
        return message;
        //}
    }

    @NotNull
    public Customer getOrCreateCustomer(String senderId, String senderName) {
        if (customerCache.containsKey(senderId) && chacheEnabled) {
            return customerCache.get(senderId);
        } else {
            Customer customer = customerRepository.findByIdentifier(senderId);
            if (customer == null) {
                customer = new Customer();
                customer.setName(senderName);
                customer.setIdentifier(senderId);
                customer = customerRepository.save(customer);
            }
            customerCache.put(senderId, customer);
            return customer;
        }
    }

    public FileInfo fetchFileInfo(String fileName) {
       /* if (fileInfoCache.containsKey(fileName)) {
            return fileInfoCache.get(fileName);
        } else {*/
        FileInfo fileInfo = fileInfoRepository.findByFilename(fileName);
            /*if (fileInfo != null) {
                fileInfoCache.put(fileName, fileInfo);
            }*/
        return fileInfo;
        //}
    }
}
