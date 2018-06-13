package com.opuscapita.peppol.email.send;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.events.EventingMessageUtil;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.email.model.CombinedEmail;
import com.opuscapita.peppol.email.model.SingleEmail;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class PersistenceReporter {
    private final static Logger logger = LoggerFactory.getLogger(PersistenceReporter.class);

    //private final EmailInfoRepository emailInfoRepository;
    //private final FileInfoRepository fileInfoRepository;
    private final ErrorHandler errorHandler;
    private final MessageQueue messageQueue;

    @Value("${peppol.eventing.queue.in.name}")
    private String eventingQueue;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public PersistenceReporter(/*@NotNull EmailInfoRepository emailInfoRepository, @NotNull FileInfoRepository fileInfoRepository,*/
                               @NotNull ErrorHandler errorHandler, @NotNull MessageQueue messageQueue) {
        //this.emailInfoRepository = emailInfoRepository;
        //this.fileInfoRepository = fileInfoRepository;
        this.errorHandler = errorHandler;
        this.messageQueue = messageQueue;
    }

    @SuppressWarnings("WeakerAccess")
    public void updateStatus(@NotNull File file, @NotNull CombinedEmail combinedEmail) {
        try {
            updateDB(file, combinedEmail);
            reportStatus(file, combinedEmail);
        } catch (Exception e) {
            logger.error("Failed to update message status in the DB: " + e.getMessage());
            errorHandler.reportWithoutContainerMessage(combinedEmail.getRecipient().getId(), e,
                    "Failed to update message status in the DB", combinedEmail.getRecipient().getAddresses(), file.getAbsolutePath());
        }
    }

    private void updateDB(@NotNull File file, @NotNull CombinedEmail combinedEmail) {
        // temporary disabled
//        for (SingleEmail singleEmail : combinedEmail.getMails()) {
//            FileInfo fileInfo = fileInfoRepository.findByFilename(singleEmail.getFileName());
//
//            EmailInfo emailInfo = new EmailInfo();
//            emailInfo.setMailFilePath(file.getAbsolutePath());
//            emailInfo.setRelatedFile(fileInfo);
//            emailInfo.setStatus("E-mail delivered");
//            emailInfoRepository.save(emailInfo);
//
//            fileInfo.setEmailInfo(emailInfo);
//            fileInfoRepository.save(fileInfo);
//        }
    }

    private void reportStatus(@NotNull File file, @NotNull CombinedEmail combinedEmail) {
        for (SingleEmail singleEmail : combinedEmail.getMails()) {
            ContainerMessage cm = singleEmail.getContainerMessage();
            EventingMessageUtil.reportEvent(cm, "Email delivered: " + file.getAbsolutePath());
            try {
                messageQueue.convertAndSend(eventingQueue, cm);
            } catch (Exception e) {
                logger.error("Failed to report to eventing: " + e.getMessage(), e);
                errorHandler.reportWithContainerMessage(cm, e, "Failed to report delivered e-mail to eventing: " + e.getMessage());
            }
        }
    }

}
