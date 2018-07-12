package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.outbound.controller.sender.OxalisWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
@Configuration
@ComponentScan(basePackages = { "com.opuscapita.peppol.outbound", "com.opuscapita.peppol.commons" })
public class OutboundAppTestConfig {

    @Bean
    MessageQueue messageQueue() {
        return mock(MessageQueue.class);
    }

    @Bean
    OxalisWrapper oxalisOutboundModuleWrapper() {
        return new OxalisWrapper();
    }

    @Bean
    OxalisErrorRecognizer oxalisErrorRecognizer() {
        OxalisErrorRecognizer mock = mock(OxalisErrorRecognizer.class);
        when(mock.recognize(any(Exception.class))).thenReturn(SendingErrors.OTHER_ERROR);
        return mock;
    }

    @Bean
    ErrorHandler errorHandler() {
        return OutboundAppTest.errorHandler;
    }

    @Bean
    StatusReporter statusReporter() {
        return OutboundAppTest.statusReporter;
    }

    @Bean
    ContainerMessageSerializer containerMessageSerializer() {
        return mock(ContainerMessageSerializer.class);
    }

}
