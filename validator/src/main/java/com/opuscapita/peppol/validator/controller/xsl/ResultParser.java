package com.opuscapita.peppol.validator.controller.xsl;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.body.ValidationRule;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sergejs.Roze
 */
@Component
public class ResultParser {
    private final SAXParserFactory saxParserFactory;

    public ResultParser(@NotNull @Lazy SAXParserFactory saxParserFactory) {
        this.saxParserFactory = saxParserFactory;
    }

    @SuppressWarnings("ConstantConditions")
    public ContainerMessage parse(@NotNull ContainerMessage cm, @NotNull InputStream xml, @NotNull ValidationRule rule)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = saxParserFactory.newSAXParser();
        Endpoint endpoint = cm.getProcessingInfo().getCurrentEndpoint();

        parser.parse(xml, new DefaultHandler() {
            ValidationError current = null;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
                if (attributes.getIndex("test") != -1) {
                    current = parseAttributes(attributes, rule);
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) {
                String line = new String(ch, start, length);
                if (current != null && StringUtils.isNotBlank(line)) {
                    current = current.withText(line);
                    if ("fatal".equals(current.getFlag())) {
                        cm.addError(current.toDocumentError(endpoint));
                    } else {
                        cm.addWarning(current.toDocumentWarning(endpoint));
                    }
                    current = null;
                }
            }
        });

        return cm;
    }

    private ValidationError parseAttributes(Attributes attr, ValidationRule rule) {
        String flag = getValue(attr, "flag");
        if ("fatal".equals(flag) || "warning".equals(flag)) {
            if (rule.getSuppress() == null || !rule.getSuppress().contains(getValue(attr, "test"))) {
                return new ValidationError("Validation error")
                        .withTest(getValue(attr, "test"))
                        .withIdentifier(getValue(attr, "id"))
                        .withLocation(getValue(attr, "location"))
                        .withFlag(flag);
            }
        }
        return null;
    }

    private String getValue(Attributes attr, String id) {
        int index = attr.getIndex(id);
        if (index == -1) {
            return "N/A";
        }
        return attr.getValue(index);
    }

}
