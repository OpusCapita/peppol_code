package com.opuscapita.peppol.validator.controller.xsl;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class ResultParser {
    private final SAXParserFactory saxParserFactory;

    public ResultParser(@NotNull @Lazy SAXParserFactory saxParserFactory) {
        this.saxParserFactory = saxParserFactory;
    }

    public ContainerMessage parse(@NotNull ContainerMessage cm, @NotNull FastByteArrayOutputStream xml) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = saxParserFactory.newSAXParser();

        parser.parse(xml.getInputStream(), new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
            }
        });

        return cm;
    }

}
