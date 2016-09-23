package com.opuscapita.peppol.events.persistence.controller;

import com.opuscapita.peppol.commons.model.*;
import com.opuscapita.peppol.events.persistence.model.AccessPointRepository;
import com.opuscapita.peppol.events.persistence.model.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TreeSet;

/**
 * Created by Daniil on 18.07.2016.
 */
@Component
public class PersistenceController {
    Logger logger = LoggerFactory.getLogger(PersistenceController.class);

    @Autowired
    AccessPointRepository accessPointRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    FileInfoRepository fileInfoRepository;
    @Autowired
    FailedFileInfoRepository failedFileInfoRepository;
    @Autowired
    SentFileInfoRepository sentFileInfoRepository;
    @Autowired
    ReprocessFileInfoRepository reprocessFileInfoRepository;
    @Value("${error.dir}")
    private String errorDirPath;
    @Value("${invalid.dir}")
    private String invalidDirPath;

    @Transactional
    public void storePeppolEvent(PeppolEvent peppolEvent) {
        refactorIfInbound(peppolEvent);
        getAccessPoint(peppolEvent);

        Message message = getOrCreateMessage(peppolEvent);
        FileInfo fileInfo = getFileInfo(message, peppolEvent);
        setFileInfoStatus(fileInfo, peppolEvent, message);
        fileInfoRepository.save(fileInfo);
        Message persistedMessage = messageRepository.save(message);
        logger.info("Message persisted with id: " + persistedMessage.getId());
    }

    private FileInfo getFileInfo(Message message, PeppolEvent peppolEvent) {
        FileInfo fileInfo = fileInfoRepository.findByFilename(peppolEvent.getFileName());
        if (fileInfo == null) {
            fileInfo = new FileInfo();
            fileInfo.setFilename(peppolEvent.getFileName());
            fileInfo.setFileSize(peppolEvent.getFileSize());
            if (message.getFiles() != null && message.getFiles().size() > 0) {
                fileInfo.setDuplicate(true);
            } else {
                fileInfo.setDuplicate(false);
            }
            fileInfo.setMessage(message);
            fileInfoRepository.save(fileInfo);
        }
        return fileInfo;
    }

    private FileInfo setFileInfoStatus(FileInfo fileInfo, PeppolEvent peppolEvent, Message message) {
        boolean validationError = true;
        message.setStatus(MessageStatus.processing);
        switch (peppolEvent.getTransportType()) {
            case OUT_REPROCESS:
                addReprocessInfo(fileInfo);
                break;
            case IN_IN:
            case OUT_IN:
                validationError = true;
                break;
            case IN_OUT:
                validationError = true;
//                if (peppolEvent.getErrorMessage() == null || peppolEvent.getErrorMessage().isEmpty()) {
                addSentInfo(fileInfo, peppolEvent);
                message.setStatus(MessageStatus.sent);
//                }
                break;
            case OUT_PEPPOL:
            case OUT_PEPPOL_FINAL:
                if (peppolEvent.getErrorMessage() == null || peppolEvent.getErrorMessage().isEmpty()) {
                    addSentInfo(fileInfo, peppolEvent);
                    message.setStatus(MessageStatus.sent);
                }
                validationError = false;
                break;
            default:
                logger.error("Unable to process transport type: " + peppolEvent.getTransportType());
                break;
        }
        if (peppolEvent.getErrorMessage() != null && !peppolEvent.getErrorMessage().isEmpty()) {
            addErrorFileInfo(fileInfo, peppolEvent, validationError);
            if (peppolEvent.getTransportType() == TransportType.OUT_PEPPOL) {
                message.setStatus(MessageStatus.reprocessed);
            } else {
                message.setStatus(validationError ? MessageStatus.invalid : MessageStatus.failed);
            }
        }
        return fileInfo;
    }

    private void addReprocessInfo(FileInfo fileInfo) {
        ReprocessFileInfo reprocessFileInfo = new ReprocessFileInfo();
        reprocessFileInfo.setReprocessedFile(fileInfo);
        reprocessFileInfo = reprocessFileInfoRepository.save(reprocessFileInfo);
        if (fileInfo.getReprocessInfo() == null) {
            fileInfo.setReprocessInfo(new TreeSet<ReprocessFileInfo>());
        }
        fileInfo.getReprocessInfo().add(reprocessFileInfo);
        fileInfoRepository.save(fileInfo);
    }

    private void addErrorFileInfo(FileInfo fileInfo, PeppolEvent peppolEvent, boolean invalid) {
        FailedFileInfo failedFileInfo = new FailedFileInfo();
        failedFileInfo.setFailedFile(fileInfo);
        failedFileInfo.setErrorFilePath(findErrorFilePath(peppolEvent.getFileName(), invalid));
        failedFileInfo.setInvalid(invalid);
        String errorMessage = peppolEvent.getErrorMessage();
        if (errorMessage.length() > 1000) {
            errorMessage = errorMessage.substring(0, 1000);
        }
        failedFileInfo.setErrorMessage(errorMessage);
        failedFileInfo = failedFileInfoRepository.save(failedFileInfo);
        if (fileInfo.getFailedInfo() == null) {
            fileInfo.setFailedInfo(new TreeSet<FailedFileInfo>());
        }
        fileInfo.getFailedInfo().add(failedFileInfo);
    }

    private String findErrorFilePath(String fileName, boolean invalid) {
        if (invalid) {
            return invalidDirPath + File.separator + fileName.substring(0, fileName.length() - 3) + "txt";
        } else {
            return errorDirPath + File.separator + fileName.substring(0, fileName.length() - 3) + "txt";
        }
    }

    protected void addSentInfo(FileInfo fileInfo, PeppolEvent peppolEvent) {
        SentFileInfo sentFileInfo = new SentFileInfo();
        sentFileInfo.setSentFile(fileInfo);
        sentFileInfo.setTransmissionId(peppolEvent.getTransactionId());
        sentFileInfo.setApProtocol(peppolEvent.getSendingProtocol());
        if (peppolEvent.getCommonName() != null) {
            // CN example: "O=Telenor Norge AS,CN=APP_1000000030,C=NO"
            sentFileInfo.setApCompanyName(getApName(peppolEvent.getCommonName()));
            sentFileInfo.setApId(getApId(peppolEvent.getCommonName()));
        }
        sentFileInfo = sentFileInfoRepository.save(sentFileInfo);
        if (fileInfo.getSentInfo() == null) {
            fileInfo.setSentInfo(new TreeSet<SentFileInfo>());
        }

        fileInfo.getSentInfo().add(sentFileInfo);
    }

    private Message getOrCreateMessage(PeppolEvent peppolEvent) {
        Message message = fetchMessageByPeppolEvent(peppolEvent);
        if (message == null) {
            Customer customer = getOrCreateCustomer(peppolEvent);
            message = new Message();
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
            message = messageRepository.save(message);
        }
        return message;
    }

    public Message fetchMessageByPeppolEvent(PeppolEvent peppolEvent) {
        try {
            Customer customer = getOrCreateCustomer(peppolEvent);
            Message message = messageRepository.findBySenderAndInvoiceNumber(customer, peppolEvent.getInvoiceId());
            return message;
        } catch (Exception e) {
            logger.error("Failed to get customer using document: " + e.getMessage());
        }
        return null;
    }

    private Customer getOrCreateCustomer(PeppolEvent peppolEvent) {
        Customer customer = customerRepository.findByIdentifier(peppolEvent.getSenderId());
        if (customer == null) {
            customer = new Customer();

            customer.setName(peppolEvent.getSenderName());
            customer.setIdentifier(peppolEvent.getSenderId());
            customer = customerRepository.save(customer);
        }
        return customer;
    }

    protected void refactorIfInbound(PeppolEvent peppolEvent) {
        if (TransportType.isInbound(peppolEvent.getTransportType())) {
            String customerId = peppolEvent.getRecipientId();
            String customerName = peppolEvent.getRecipientName();
            String customerCountryCode = peppolEvent.getRecipientCountryCode();
            peppolEvent.setRecipientId(peppolEvent.getSenderId());
            peppolEvent.setRecipientName(peppolEvent.getSenderName());
            peppolEvent.setRecipientCountryCode(peppolEvent.getSenderCountryCode());
            peppolEvent.setSenderId(customerId);
            peppolEvent.setSenderName(customerName);
            peppolEvent.setSenderCountryCode(customerCountryCode);
        }

    }

    protected AccessPoint getAccessPoint(PeppolEvent peppolEvent) {
        AccessPoint accessPoint = null;
        if (peppolEvent.getCommonName() != null) {
            final String apId = getApId(peppolEvent.getCommonName());
            accessPoint = accessPointRepository.findByAccessPointId(apId);
            if (accessPoint == null) {
                accessPoint = new AccessPoint();
                accessPoint.setAccessPointId(apId);
                accessPoint.setAccessPointName(getApName(peppolEvent.getCommonName()));
                accessPoint = accessPointRepository.save(accessPoint);
            }
        }
        return accessPoint;
    }

    protected String getApId(String commonName) {
        String[] parts = commonName.split(",");
        if (parts.length > 1) {
            String[] idParts = parts[1].split("=");
            if (idParts.length > 1) {
                return idParts[1];
            }
        }
        return commonName;
    }

    protected String getApName(String commonName) {
        String[] parts = commonName.split(",");
        if (parts.length > 0) {
            String[] nameParts = parts[0].split("=");
            if (nameParts.length > 1) {
                return nameParts[1];
            }
        }
        return null;
    }
}
