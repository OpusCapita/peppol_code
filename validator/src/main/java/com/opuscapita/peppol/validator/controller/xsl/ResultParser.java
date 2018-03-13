package com.opuscapita.peppol.validator.controller.xsl;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.body.ValidationRule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergejs.Roze
 */
@Component
public class ResultParser {
    @Value("${peppol.validator.combine.errors.at:10}")
    private int combineThreshold;

    private final SAXParserFactory saxParserFactory;

    public ResultParser(@NotNull @Lazy SAXParserFactory saxParserFactory) {
        this.saxParserFactory = saxParserFactory;
    }

    @SuppressWarnings("ConstantConditions")
    public ContainerMessage parse(@NotNull ContainerMessage cm, @NotNull InputStream xml, @NotNull ValidationRule rule)
            throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = saxParserFactory.newSAXParser();
        Endpoint endpoint = cm.getProcessingInfo().getCurrentEndpoint();

        // key -> (skipped count, list of records)
        Map<String, MutablePair<Integer, List<ValidationError>>> errorsAndWarnings = new HashMap<>();

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
                if (current != null) {
                    String line = new String(ch, start, length);
                    if (StringUtils.isNotBlank(line)) {
                        collect(errorsAndWarnings, current.withText(line));
                        current = null;
                    }
                }
            }
        });

        return flush(cm, errorsAndWarnings, endpoint);
    }

    private void collect(@NotNull Map<String, MutablePair<Integer, List<ValidationError>>> errorsAndWarnings, @NotNull ValidationError record) {
        // new error or warning record of this type
        String key = record.getIdentifier();
        if (errorsAndWarnings.get(key) == null) {
            MutablePair<Integer, List<ValidationError>> pair = new MutablePair<>();
            List<ValidationError> list = new ArrayList<>();
            list.add(record);
            pair.setRight(list);
            pair.setLeft(0);
            errorsAndWarnings.put(key, pair);
            return;
        }

        // such error is known but there are not so many of them to combine
        MutablePair<Integer, List<ValidationError>> pair = errorsAndWarnings.get(key);
        List<ValidationError> list = pair.getRight();
        if (list.size() < combineThreshold) {
            list.add(record);
            return;
        }

        // we went over the limit, let's just count skipped values
        pair.setLeft(pair.getLeft() + 1);
    }

    private ContainerMessage flush(@NotNull ContainerMessage cm, @NotNull Map<String, MutablePair<Integer, List<ValidationError>>> errorsAndWarnings,
                       @NotNull Endpoint endpoint) {
        for (MutablePair<Integer, List<ValidationError>> pair : errorsAndWarnings.values()) {
            List<ValidationError> list = pair.getRight();
            for (int i = 0; i < list.size(); i++) {
                ValidationError record = list.get(i);

                // last record in the list
                if (i == list.size() - 1) {
                    if (pair.getLeft() != 0) {
                        record.withLocation(record.getLocation() + " (" + pair.getLeft() + " SKIPPED)");
                    }
                }

                if ("fatal".equals(record.getFlag())) {
                    cm.addError(record.toDocumentError(endpoint));
                } else {
                    cm.addWarning(record.toDocumentWarning(endpoint));
                }
            }
        }

        return cm;
    }

    private ValidationError parseAttributes(Attributes attr, ValidationRule rule) {
        String flag = getValue(attr, "flag");
        if ("fatal".equals(flag) || "warning".equals(flag)) {
            if (rule.getSuppress() == null || !rule.getSuppress().contains(getValue(attr, "id"))) {
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

    // for unit tests
    void setCombineThreshold(int combineThreshold) {
        this.combineThreshold = combineThreshold;
    }
}
