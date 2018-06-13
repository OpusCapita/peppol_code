package com.opuscapita.peppol.email.prepare;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.model.Customer;
import com.opuscapita.peppol.email.db.CustomerRepository;
import com.opuscapita.peppol.email.model.Recipient;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
public class CustomerEmailCreator {
    private final CustomerRepository customerRepository;
    private final EmailCreator emailCreator;
    private final OxalisErrorRecognizer oxalisErrorRecognizer;

    @Value("${peppol.email-notificator.out.invalid.subject}")
    private String outInvalidEmailSubject;
    @Value("${peppol.email-notificator.in.invalid.subject}")
    private String inInvalidEmailSubject;
    @Value("${peppol.email-notificator.out.lookup-error.subject}")
    private String outLookupErrorEmailSubject;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public CustomerEmailCreator(@NotNull CustomerRepository customerRepository,
                                @NotNull EmailCreator emailCreator,
                                @NotNull OxalisErrorRecognizer oxalisErrorRecognizer) {
        this.customerRepository = customerRepository;
        this.emailCreator = emailCreator;
        this.oxalisErrorRecognizer = oxalisErrorRecognizer;
    }

    public void create(@NotNull ContainerMessage cm, boolean createTicket) throws IOException {
        if (!cm.hasErrors()) {
            emailCreator.fail(cm, "Document has no errors: " + cm.getFileName() + ", nothing to report", null);
            return;
        }

        Customer customer = findCustomer(cm);
        if (customer == null) {
            return;
        }
        String addresses = cm.isInbound() ? customer.getInboundEmails() : customer.getOutboundEmails();

        Recipient recipient = new Recipient(Recipient.Type.CUSTOMER, Integer.toString(customer.getId()), customer.getName(), addresses);

        emailCreator.create(recipient, cm, generateSubject(cm), EmailTemplates.format(cm), createTicket);
    }

    private String generateSubject(ContainerMessage cm) {
        if (cm.isInbound()) {
            return inInvalidEmailSubject;
        }
        String errorMessage = cm.getProcessingInfo() == null ? "" : cm.getProcessingInfo().getProcessingException();
        if (errorMessage == null) {
            errorMessage = "";
        }
        if (oxalisErrorRecognizer.recognize(errorMessage) == SendingErrors.UNKNOWN_RECIPIENT) {
            return outLookupErrorEmailSubject;
        }
        return outInvalidEmailSubject;
    }

    @Nullable
    private Customer findCustomer(@NotNull ContainerMessage cm) {
        String customerId = cm.getCustomerId();
        if (StringUtils.isBlank(customerId)) {
            emailCreator.fail(cm, "Cannot determine customer ID from the file: " + cm.getFileName(), null);
            return null;
        }

        Customer customer = customerRepository.findByIdentifier(customerId);
        if (customer == null) {
            emailCreator.fail(cm, "Customer for file " + cm.getFileName() + " not found in the database: " + customerId, null);
            return null;
        }

        @SuppressWarnings("ConstantConditions") String emails = cm.isInbound() ? customer.getInboundEmails() : customer.getOutboundEmails();
        if (StringUtils.isBlank(emails)) {
            emailCreator.fail(cm, "E-mail address not set for the customer: " + customerId + " " + customer.getName(),
                    "Please update the email for the customer " + customerId + " " + customer.getName() +
                            "\nAfterwards reprocess the data file " + cm.getFileName() +
                            " from the UI to send the email notification to the customer automatically.");
            return null;
        }

        return customer;
    }

}
