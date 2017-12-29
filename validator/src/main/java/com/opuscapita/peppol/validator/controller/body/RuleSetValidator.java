package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.cache.XsdRepository;
import com.opuscapita.peppol.validator.controller.cache.XslRepository;
import com.opuscapita.peppol.validator.controller.xsd.XsdValidator;
import com.opuscapita.peppol.validator.controller.xsl.ResultParser;
import com.opuscapita.peppol.validator.controller.xsl.XslValidator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.util.List;

/**
 * Validates document using set of XSL rules.
 *
 * @author Sergejs.Roze
 */
@Component
public class RuleSetValidator {
    private final static Logger logger = LoggerFactory.getLogger(RuleSetValidator.class);

    private final RuleSetConfig config;
    private final XslRepository xslRepository;
    private final XslValidator xslValidator;
    private final ResultParser resultParser;
    private final XsdValidator xsdValidator;
    private final XsdRepository xsdRepository;

    @Autowired
    public RuleSetValidator(@NotNull RuleSetConfig config, @NotNull XslRepository xslRepository, @NotNull XslValidator xslValidator,
                            @NotNull ResultParser resultParser, @NotNull XsdValidator xsdValidator, @NotNull XsdRepository xsdRepository) {
        this.config = config;
        this.xslRepository = xslRepository;
        this.xslValidator = xslValidator;
        this.resultParser = resultParser;
        this.xsdValidator = xsdValidator;
        this.xsdRepository = xsdRepository;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] body, @NotNull ContainerMessage cm) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        String documentType = cm.getDocumentInfo().getProfileId() + "###" + cm.getDocumentInfo().getCustomizationId();
        List<String> rules = config.getRules(documentType);
        if (rules == null) {
            throw new IllegalArgumentException("Validation rule set not found for type: " + documentType + ", of " + cm.getFileName());
        }

        for (String rule : rules) {
            logger.debug("Running rule " + rule + " against " + cm.getFileName());
            if (rule.toLowerCase().endsWith(".xsl")) {
                Templates template = xslRepository.getByFileName(rule);
                cm = resultParser.parse(cm, xslValidator.validate(body, template).getInputStream());
            } else {
                if (rule.toLowerCase().endsWith(".xsd")) {
                    Schema schema = xsdRepository.getByName(rule);
                    ValidationError error = xsdValidator.validate(body, schema);
                    if (error != null) {
                        cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
                    }
                } else {
                    logger.warn("Ignoring unknown rule format: " + rule + " when validating file " + cm.getFileName() + ", supported formats are 'xsl' and 'xsd'");
                }
            }
        }

        return cm;
    }


}