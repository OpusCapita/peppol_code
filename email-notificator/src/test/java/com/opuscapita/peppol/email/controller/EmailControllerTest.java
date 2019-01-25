package com.opuscapita.peppol.email.controller;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.email.db.AccessPointRepository;
import com.opuscapita.peppol.email.db.CustomerRepository;
import com.opuscapita.peppol.email.prepare.AccessPointEmailCreator;
import com.opuscapita.peppol.email.prepare.CustomerEmailCreator;
import com.opuscapita.peppol.email.prepare.EmailCreator;
import org.junit.Test;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EmailControllerTest {
    private CustomerRepository customerRepository = mock(CustomerRepository.class);
    private AccessPointRepository accessPointRepository = mock(AccessPointRepository.class);
    private OxalisErrorRecognizer oxalisErrorRecognizer = mock(OxalisErrorRecognizer.class);
    private ErrorHandler errorHandler = mock(ErrorHandler.class);

    @Test
    public void testOnlyOneTicketCreatedOnError() {
        EmailCreator emailCreator = new EmailCreator(errorHandler, new Gson());
        CustomerEmailCreator customerEmailCreator = new CustomerEmailCreator(customerRepository, emailCreator, oxalisErrorRecognizer);
        AccessPointEmailCreator accessPointEmailCreator = new AccessPointEmailCreator(emailCreator, customerRepository, accessPointRepository);

        EmailController controller = new EmailController(customerEmailCreator, accessPointEmailCreator);
        controller.setOutboundActions(EmailActions.EMAIL_AP);

        ContainerMessage cm = new ContainerMessage("file_name", Endpoint.TEST);

        try {
            controller.processMessage(cm);
        } catch (Exception e) {
            fail("Should not throw exception on regular processing");
        }

        verify(errorHandler, times(1)).reportWithContainerMessage(any(), any(), anyString(), anyString());
    }
}