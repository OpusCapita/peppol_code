package com.opuscapita.peppol.preprocessing.controller;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.storage.Storage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan(basePackages = {
        "com.opuscapita.peppol.commons.container",
        "com.opuscapita.peppol.commons.config",
        "com.opuscapita.peppol.preprocessing.controller"
})
@EnableConfigurationProperties
public class PreprocessingControllerTestConfig {

    @Bean
    MessageQueue messageQueue() {
        return mock(MessageQueue.class);
    }

    @Bean
    Storage storage() throws IOException {
        Storage result = mock(Storage.class);
        when(result.moveToLongTerm(anyString(), anyString(), anyString())).thenReturn("nowhere");
        return result;
    }

    @Bean
    Gson gson() {
        return new Gson();
    }
}
