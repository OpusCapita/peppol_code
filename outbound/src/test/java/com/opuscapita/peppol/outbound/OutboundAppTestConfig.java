package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessageSerializer;
import com.opuscapita.peppol.commons.container.process.StatusReporter;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import com.opuscapita.peppol.commons.errors.oxalis.OxalisErrorRecognizer;
import com.opuscapita.peppol.commons.errors.oxalis.SendingErrors;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import eu.peppol.outbound.transmission.OxalisOutboundModuleWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Sergejs.Roze
 */
@Configuration
@ComponentScan(basePackages = { "com.opuscapita.peppol.outbound" })
@EnableAsync
public class OutboundAppTestConfig {

    @Bean
    MessageQueue messageQueue() {
        return mock(MessageQueue.class);
    }

    @Bean
    OxalisOutboundModuleWrapper oxalisOutboundModuleWrapper() {
        return new OxalisOutboundModuleWrapper();
    }

    @Bean
    OxalisErrorRecognizer oxalisErrorRecognizer() {
        OxalisErrorRecognizer mock = mock(OxalisErrorRecognizer.class);
        when(mock.recognize(any(Exception.class))).thenReturn(SendingErrors.OTHER_ERROR);
        return mock;
    }

    @Bean
    ErrorHandler errorHandler() {
        return mock(ErrorHandler.class);
    }

    @Bean
    StatusReporter statusReporter() {
        return mock(StatusReporter.class);
    }

    @Bean
    ContainerMessageSerializer containerMessageSerializer() {
        return mock(ContainerMessageSerializer.class);
    }

}
