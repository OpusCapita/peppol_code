package com.opuscapita.peppol.email.prepare;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.ApInfo;
import com.opuscapita.peppol.commons.model.AccessPoint;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.email.db.AccessPointRepository;
import com.opuscapita.peppol.email.db.CustomerRepository;
import com.opuscapita.peppol.email.model.Recipient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
@Lazy
public class AccessPointEmailCreator {

    private static final Logger logger = LoggerFactory.getLogger(AccessPointEmailCreator.class);

    private final EmailCreator emailCreator;
    private final CustomerRepository customerRepository;
    private final AccessPointRepository accessPointRepository;

    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public AccessPointEmailCreator(@NotNull EmailCreator emailCreator, @NotNull CustomerRepository customerRepository, @NotNull AccessPointRepository accessPointRepository) {
        this.emailCreator = emailCreator;
        this.customerRepository = customerRepository;
        this.accessPointRepository = accessPointRepository;
    }

    public void create(@NotNull ContainerMessage cm, boolean createTicket) throws IOException {
        if (!cm.hasErrors()) {
            emailCreator.fail(cm, "No error found to report in the document: " + cm.toLog(), null);
            return;
        }
        if (cm.getProcessingInfo() == null) {
            emailCreator.fail(cm, "No processing info found in the document: " + cm.toLog(), null);
            return;
        }

        AccessPoint accessPoint = findAccessPointInfo(cm);
        if (accessPoint == null) {
            emailCreator.fail(cm, "Failed to determine access point by CN='" + cm.getProcessingInfo().getApInfo() + "' for " + cm.toLog(), null);
            return;
        }

        String addresses = accessPoint.getEmailList();
        if (StringUtils.isBlank(addresses)) {
            emailCreator.fail(cm, "E-mail address not set for the AP " + accessPoint.getAccessPointId() + " " + accessPoint.getAccessPointName(),
                    "Please update the e-mail for the Access Point (AP): " + accessPoint.getAccessPointId() + ", name: " + accessPoint.getAccessPointName() +
                            "\nAfterwards reprocess the data for " + cm.toLog() + " from the UI to send the e-mail notifications to this AP automatically");
            return;
        }

        Recipient recipient = new Recipient(Recipient.Type.AP, accessPoint.getAccessPointId(), accessPoint.getAccessPointName(), addresses);
        emailCreator.create(recipient, cm, inInvalidEmailSubject, EmailTemplates.format(cm), createTicket);
    }

    private AccessPoint findAccessPointInfo(@NotNull ContainerMessage cm) {
        ApInfo apInfo = cm.getProcessingInfo().getApInfo();
        if (apInfo != null) {
            AccessPoint accessPoint = accessPointRepository.findByAccessPointId(apInfo.getId());
            logger.info("Found access point info from common name: [" + apInfo + "] for " + cm.toLog());
            return accessPoint;
        }

        if (StringUtils.isNotBlank(cm.getCustomerId())) {
            Customer customer = customerRepository.findByIdentifier(cm.getCustomerId());
            if (customer != null && customer.getAccessPoint() != null) {
                AccessPoint accessPoint = customer.getAccessPoint();
                logger.info("Found access point info from db: '" + accessPoint + "' for " + cm.toLog());
                return accessPoint;
            }
        }

        return null;
    }

}
