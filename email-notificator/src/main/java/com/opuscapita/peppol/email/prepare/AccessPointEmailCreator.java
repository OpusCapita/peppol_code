package com.opuscapita.peppol.email.prepare;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.ApInfo;
import com.opuscapita.peppol.commons.model.AccessPoint;
import com.opuscapita.peppol.email.db.AccessPointRepository;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
    private final EmailCreator emailCreator;
    private final AccessPointRepository accessPointRepository;

    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public AccessPointEmailCreator(@NotNull EmailCreator emailCreator, @NotNull AccessPointRepository accessPointRepository) {
        this.emailCreator = emailCreator;
        this.accessPointRepository = accessPointRepository;
    }

    public void create(@NotNull ContainerMessage cm, boolean createTicket) throws IOException {
        if (!cm.hasErrors()) {
            emailCreator.fail(cm, "Document has no errors: " + cm.getFileName() + ", nothing to report");
        }
        if (cm.getProcessingInfo() == null) {
            emailCreator.fail(cm, "No processing info in document: " + cm.getFileName());
        }
        if (StringUtils.isBlank(cm.getProcessingInfo().getCommonName())) {
            emailCreator.fail(cm, "Access Point information is not present in the document, cannot create e-mail to AP for " + cm.getFileName());
        }

        ApInfo apInfo = ApInfo.parseFromCommonName(cm.getProcessingInfo().getCommonName());
        String id = apInfo.getId();
        AccessPoint accessPoint = accessPointRepository.findByAccessPointId(id);
        if (accessPoint == null) {
            emailCreator.fail(cm, "Failed to determine access point by CN='" + cm.getProcessingInfo().getCommonName() +
                    "' for " + cm.getFileName());
        }

        @SuppressWarnings("ConstantConditions") String recipients = accessPoint.getEmailList();
        if (StringUtils.isBlank(recipients)) {
            emailCreator.fail(cm, "E-mail address not set for the Access Point: " + id + ", file: " + cm.getFileName());
        }

        emailCreator.create("ap_" + id, cm, recipients, inInvalidEmailSubject, BodyFormatter.format(cm), createTicket);
    }

}