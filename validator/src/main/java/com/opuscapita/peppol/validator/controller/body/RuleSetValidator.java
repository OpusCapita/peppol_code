package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.validator.controller.cache.XslRepository;
import com.opuscapita.peppol.validator.controller.xsl.ResultParser;
import com.opuscapita.peppol.validator.controller.xsl.XslValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

/**
 * Validates document using set of XSL rules.
 *
 * @author Sergejs.Roze
 */
@Component
public class RuleSetValidator {
    private final RuleSetConfig config;
    private final XslRepository xslRepository;
    private final XslValidator xslValidator;
    private final ResultParser resultParser;

    @Autowired
    public RuleSetValidator(@NotNull RuleSetConfig config, @NotNull XslRepository xslRepository, @NotNull XslValidator xslValidator,
                            @NotNull ResultParser resultParser) {
        this.config = config;
        this.xslRepository = xslRepository;
        this.xslValidator = xslValidator;
        this.resultParser = resultParser;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] body, @NotNull ContainerMessage cm) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        String documentType = cm.getDocumentInfo().getArchetype() + "." + cm.getDocumentInfo().getDocumentType();
        List<String> rules = config.getRules(documentType);
        if (rules == null) {
            throw new IllegalArgumentException("Validation rule set is not found for type: " + documentType + ", of " + cm.getFileName());
        }

        for (String rule : rules) {
            Templates template = xslRepository.getByName(rule);
            cm = resultParser.parse(cm, xslValidator.validate(body, template).getInputStream());
        }

        return cm;
    }


}
