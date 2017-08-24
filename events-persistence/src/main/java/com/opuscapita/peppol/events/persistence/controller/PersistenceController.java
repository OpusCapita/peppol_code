package com.opuscapita.peppol.events.persistence.controller;


import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.process.route.ProcessType;
import com.opuscapita.peppol.commons.model.*;
import com.opuscapita.peppol.events.persistence.model.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by Daniil on 18.07.2016.
 */
@Component
public class PersistenceController {
    private static final List<ProcessType> FINAL_STATUSES = new ArrayList<ProcessType>() {
        {
            add(ProcessType.IN_OUT);
            add(ProcessType.OUT_PEPPOL);
            add(ProcessType.OUT_PEPPOL_FINAL);
            add(ProcessType.OUT_OUTBOUND);
            add(ProcessType.IN_MQ_TO_FILE);
        }
    };
    private final Logger logger = LoggerFactory.getLogger(PersistenceController.class);

    private final AccessPointRepository accessPointRepository;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final FileInfoRepository fileInfoRepository;
    private final FailedFileInfoRepository failedFileInfoRepository;
    private final SentFileInfoRepository sentFileInfoRepository;
    private final ReprocessFileInfoRepository reprocessFileInfoRepository;

    @Value("${peppol.events-persistence.error.dir}")
    private String errorDirPath;
    @Value("${peppol.events-persistence.invalid.dir}")
    private String invalidDirPath;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public PersistenceController(
            AccessPointRepository accessPointRepository,
            MessageRepository messageRepository,
            CustomerRepository customerRepository,
            FileInfoRepository fileInfoRepository,
            FailedFileInfoRepository failedFileInfoRepository,
            SentFileInfoRepository sentFileInfoRepository,
            ReprocessFileInfoRepository reprocessFileInfoRepository
    ) {
        this.accessPointRepository = accessPointRepository;
        this.messageRepository = messageRepository;
        this.customerRepository = customerRepository;
        this.fileInfoRepository = fileInfoRepository;
        this.failedFileInfoRepository = failedFileInfoRepository;
        this.sentFileInfoRepository = sentFileInfoRepository;
        this.reprocessFileInfoRepository = reprocessFileInfoRepository;

    }

    @Transactional
    @Retryable(include = ConnectException.class, maxAttempts = 5, backoff = @Backoff(10000L))
    public void storePeppolEvent(PeppolEvent peppolEvent) throws Exception {
        logger.info("About to process peppol event " + peppolEvent);
        if (/*peppolEvent.getProcessType() == ProcessType.IN_INBOUND ||*/
                peppolEvent.getProcessType() == ProcessType.OUT_FILE_TO_MQ ||
                        peppolEvent.getProcessType() == ProcessType.REST) {
            // some events cannot be processed
            logger.info("Skipping persisting of Peppol event for unsupported type [" + peppolEvent.getProcessType().name() + "]");
            return;
        }

        swapSenderAndReceiverForInbound(peppolEvent);
        AccessPoint accessPoint = getAccessPoint(peppolEvent);
        Message message = getOrCreateMessage(peppolEvent);
        FileInfo fileInfo = getFileInfo(message, peppolEvent);
        try {
            setFileInfoStatus(fileInfo, peppolEvent, message);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return;
        }
        fileInfoRepository.save(fileInfo);
        Message persistedMessage = messageRepository.save(message);
        logger.info("Message[" + peppolEvent.getFileName() + " : AP - " + (accessPoint != null ? accessPoint : "N/A") + "] persisted with id: " + persistedMessage.getId());
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

    private void setFileInfoStatus(FileInfo fileInfo, PeppolEvent peppolEvent, Message message) throws Exception {
        boolean validationError = true;
        if (message.getStatus() != MessageStatus.sent && message.getStatus() != MessageStatus.reprocessed && message.getStatus() != MessageStatus.resolved) {
            message.setStatus(MessageStatus.processing);
        }
        switch (peppolEvent.getProcessType()) {
            case OUT_REPROCESS:
            case IN_REPROCESS:
                addReprocessInfo(fileInfo);
                break;
            case IN_IN:
            case IN_INBOUND:
            case OUT_IN:
            case IN_VALIDATION:
            case OUT_VALIDATION:
            case IN_PREPROCESS:
            case OUT_PREPROCESS:
            case IN_ROUTING:
            case OUT_ROUTING:
            case OUT_PEPPOL_RETRY:
            case IN_TEST:
            case OUT_TEST:
            case TEST:
                break;
            case IN_OUT:
            case IN_MQ_TO_FILE:
                validationError = true;
                addSentInfo(fileInfo, peppolEvent);
                message.setStatus(MessageStatus.sent);
                break;
            case OUT_PEPPOL:
            case OUT_PEPPOL_FINAL:
            case OUT_OUTBOUND:
                if (peppolEvent.getErrorMessage() == null || peppolEvent.getErrorMessage().isEmpty()) {
                    addSentInfo(fileInfo, peppolEvent);
                    message.setStatus(MessageStatus.sent);
                }
                validationError = false;
                break;
            default:
                logger.warn("Unable to process transport type: " + peppolEvent.getProcessType());
                break;
        }

        if (peppolEvent.getErrorMessage() != null && !peppolEvent.getErrorMessage().isEmpty()) {
            addErrorFileInfo(fileInfo, peppolEvent, validationError);
            if (peppolEvent.getProcessType() == ProcessType.OUT_PEPPOL || peppolEvent.getProcessType() == ProcessType.OUT_OUTBOUND) {
                message.setStatus(MessageStatus.reprocessed);
            } else {
                if (message.getStatus() != MessageStatus.sent && message.getStatus() != MessageStatus.resolved) {
                    message.setStatus(validationError ? MessageStatus.invalid : MessageStatus.failed);
                }
            }
        }

    }

    private void addReprocessInfo(FileInfo fileInfo) {
        ReprocessFileInfo reprocessFileInfo = new ReprocessFileInfo();
        reprocessFileInfo.setReprocessedFile(fileInfo);
        reprocessFileInfo = reprocessFileInfoRepository.save(reprocessFileInfo);
        if (fileInfo.getReprocessInfo() == null) {
            fileInfo.setReprocessInfo(new TreeSet<>());
        }
        fileInfo.getReprocessInfo().add(reprocessFileInfo);
        fileInfoRepository.save(fileInfo);
    }

    private void addErrorFileInfo(FileInfo fileInfo, PeppolEvent peppolEvent, boolean invalid) {
        FailedFileInfo failedFileInfo = new FailedFileInfo();
        failedFileInfo.setFailedFile(fileInfo);
        failedFileInfo.setErrorFilePath(findErrorFilePath(peppolEvent, invalid));
        failedFileInfo.setInvalid(invalid);
        String errorMessage = peppolEvent.getErrorMessage();

        String[] errors = errorMessage.split(DocumentError.ERROR_SEPARATOR);
        if (errors.length > 2) {                                      //taking only first error
            errorMessage = errors[0] + DocumentError.ERROR_SEPARATOR + errors[1];
        }

        if (errorMessage.length() > 1000) {
            errorMessage = errorMessage.substring(0, 1000);
        }
        failedFileInfo.setErrorMessage(errorMessage);

        failedFileInfo = failedFileInfoRepository.save(failedFileInfo);
        if (fileInfo.getFailedInfo() == null) {
            fileInfo.setFailedInfo(new TreeSet<>());
        }
        fileInfo.getFailedInfo().add(failedFileInfo);
    }

    private String findErrorFilePath(PeppolEvent event, boolean invalid) {
        String baseName = FilenameUtils.getBaseName(event.getFileName());

        String result;
        if (invalid) {
            result = invalidDirPath + File.separator + baseName + ".txt";
        } else {
            result = errorDirPath + File.separator + baseName + ".txt";
        }

        // this should provide backward compatibility
        if (new File(result).exists()) {
            logger.info("PersistenceController: error file path set to: " + result);
            return result;
        }

        // for the new version - write out the error message to file
        String message = event.getErrorMessage();
        if (StringUtils.isNotBlank(message)) {
            try (OutputStream outputStream = new FileOutputStream(result)) {
                outputStream.write(message.getBytes());
                logger.info("PersistenceController: error file path set to: " + result);
                return result;
            } catch (Exception e) {
                logger.error("Failed to store error message in file: ", e);
            }
        }
        return "";
    }

    private void addSentInfo(FileInfo fileInfo, PeppolEvent peppolEvent) throws Exception {
        if (peppolEvent.getTransactionId() == null && FINAL_STATUSES.contains(peppolEvent.getProcessType())) {
            //Terminal statuses imply that we do have transaction id
            throw new Exception("For file: " + peppolEvent.getFileName() + " and status: " + peppolEvent.getProcessType() + " the transaction id is NULL!");
        }
        SentFileInfo sentFileInfo = new SentFileInfo();
        sentFileInfo.setSentFile(fileInfo);
        String prefix = peppolEvent.getProcessType().name().split("_")[0];
        //Some intermediate phase events might miss the transaction id
        if (peppolEvent.getTransactionId() != null) {
            sentFileInfo.setTransmissionId(prefix + peppolEvent.getTransactionId());
        }
        sentFileInfo.setApProtocol(peppolEvent.getSendingProtocol());
        if (peppolEvent.getCommonName() != null) {
            // CN example: "O=Telenor Norge AS,CN=APP_1000000030,C=NO"
            ApInfo apInfo = ApInfo.parseFromCommonName(peppolEvent.getCommonName());
            sentFileInfo.setApCompanyName(apInfo.getName());
            sentFileInfo.setApId(apInfo.getId());
        }
        sentFileInfo = sentFileInfoRepository.save(sentFileInfo);
        if (fileInfo.getSentInfo() == null) {
            fileInfo.setSentInfo(new TreeSet<>());
        }

        fileInfo.getSentInfo().add(sentFileInfo);
    }

    @NotNull
    private Message getOrCreateMessage(@NotNull PeppolEvent peppolEvent) {
        Message message = fetchMessageByPeppolEvent(peppolEvent);
        if (message == null) {
            logger.info("Creating new message for file: " + peppolEvent.getFileName());
            Customer customer = getOrCreateCustomer(peppolEvent);
            message = new Message();
            message.setSender(customer);
            message.setRecipientId(peppolEvent.getRecipientId());
            message.setInvoiceNumber(peppolEvent.getInvoiceId());
            message.setDocumentType(peppolEvent.getDocumentType());
            message.setStatus(MessageStatus.processing);

            if (peppolEvent.getProcessType().isInbound()) {
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

            // not all messages have due date
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

    @Nullable
    private Message fetchMessageByPeppolEvent(@NotNull PeppolEvent peppolEvent) {
        try {
            Customer customer = getOrCreateCustomer(peppolEvent);
            return messageRepository.findBySenderAndInvoiceNumber(customer, peppolEvent.getInvoiceId());
        } catch (Exception e) {
            logger.error("Failed to get customer using document: " + e.getMessage());
        }
        return null;
    }

    @NotNull
    private Customer getOrCreateCustomer(@NotNull PeppolEvent peppolEvent) {
        Customer customer = customerRepository.findByIdentifier(peppolEvent.getSenderId());
        if (customer == null) {
            customer = new Customer();
            customer.setName(peppolEvent.getSenderName());
            customer.setIdentifier(peppolEvent.getSenderId());
            customer = customerRepository.save(customer);
        }
        return customer;
    }

    @SuppressWarnings("deprecation")
    private void swapSenderAndReceiverForInbound(PeppolEvent peppolEvent) {
        if (peppolEvent.getProcessType() == null) {
            peppolEvent.setProcessType(ProcessType.UNKNOWN);
        }
        if (peppolEvent.getProcessType().isInbound()) {
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

    @SuppressWarnings("UnusedReturnValue")
    private AccessPoint getAccessPoint(PeppolEvent peppolEvent) {
        AccessPoint accessPoint = null;
        if (peppolEvent.getCommonName() != null) {
            ApInfo apInfo = ApInfo.parseFromCommonName(peppolEvent.getCommonName());
            final String apId = apInfo.getId();//getApId(peppolEvent.getCommonName());
            accessPoint = accessPointRepository.findByAccessPointId(apId);
            if (accessPoint == null) {
                accessPoint = new AccessPoint();
                accessPoint.setAccessPointId(apId);
                accessPoint.setAccessPointName(apInfo.getName());
                accessPoint = accessPointRepository.save(accessPoint);
            }
        }
        return accessPoint;
    }


    /*private String getApId(String commonName) {
        String[] parts = commonName.split(",");
        if (parts.length > 1) {
            String[] idParts = parts[1].split("=");
            if (idParts.length > 1) {
                return idParts[1];
            }
        }
        return commonName;
    }

    private String getApName(String commonName) {
        String[] parts = commonName.split(",");
        if (parts.length > 0) {
            String[] nameParts = parts[0].split("=");
            if (nameParts.length > 1) {
                return nameParts[1];
            }
        }
        return null;
    }*/

}
