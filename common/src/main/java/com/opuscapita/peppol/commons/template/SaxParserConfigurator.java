package com.opuscapita.peppol.commons.template;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.xml.parsers.SAXParserFactory;

/**
 * @author Sergejs.Roze
 */
@Configuration
public class SaxParserConfigurator {
    @Bean
    @Lazy
    @NotNull
    SAXParserFactory saxParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory;
    }

    /*@Bean
    @NotNull
    @Scope("prototype")
    @Lazy
    SAXParser saxParser(@NotNull SAXParserFactory saxParserFactory) throws ParserConfigurationException, SAXException {
        return saxParserFactory.newSAXParser();
    }*/
}
