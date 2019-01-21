package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import com.opuscapita.peppol.commons.mq.MessageQueue;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.cache.XsdRepository;
import com.opuscapita.peppol.validator.controller.cache.XslRepository;
import com.opuscapita.peppol.validator.controller.xsd.XsdValidator;
import com.opuscapita.peppol.validator.controller.xsl.ResultParser;
import com.opuscapita.peppol.validator.controller.xsl.XslValidator;
import net.sf.saxon.trans.XPathException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Validates document using set of XSL rules.
 *
 * @author Sergejs.Roze
 */
@Component
public class ValidationRuleExecutor {

    private final static Logger logger = LoggerFactory.getLogger(ValidationRuleExecutor.class);

    private final ValidationRules config;
    private final XslRepository xslRepository;
    private final XslValidator xslValidator;
    private final ResultParser resultParser;
    private final XsdValidator xsdValidator;
    private final XsdRepository xsdRepository;
    private final MessageQueue messageQueue;

    @Value("${peppol.email-notificator.queue.in.name}")
    private String emailNotificatorQueue;

    @Autowired
    public ValidationRuleExecutor(@NotNull ValidationRules validationRules, @NotNull XslRepository xslRepository, @NotNull XslValidator xslValidator,
                                  @NotNull ResultParser resultParser, @NotNull XsdValidator xsdValidator, @NotNull XsdRepository xsdRepository,
                                  @NotNull MessageQueue messageQueue) {
        this.config = validationRules;
        this.xslRepository = xslRepository;
        this.xslValidator = xslValidator;
        this.resultParser = resultParser;
        this.xsdValidator = xsdValidator;
        this.xsdRepository = xsdRepository;
        this.messageQueue = messageQueue;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] body, @NotNull ContainerMessage cm, @NotNull Endpoint endpoint)
            throws TransformerException, IOException, SAXException, ParserConfigurationException, TimeoutException {

        String documentType = cm.getDocumentInfo().getProfileId() + "###" + cm.getDocumentInfo().getCustomizationId();

        ValidationRule rule = config.getByDocumentType(documentType);
        if (rule == null) {
            String message = "Validation rule not found for document type: " + documentType + ", of " + cm.getFileName();
            cm.getProcessingInfo().setProcessingException(message);
            messageQueue.convertAndSend(emailNotificatorQueue, cm);
            throw new IllegalArgumentException(message);
        }

        for (String file : rule.getRules()) {
            logger.debug("Running check " + file + " against " + cm.getFileName());
            if (file.toLowerCase().endsWith(".xsl")) {
                Templates template = xslRepository.getByFileName(file);
                cm = executeXsl(body, cm, template, rule, file, endpoint);
            } else {
                if (file.toLowerCase().endsWith(".xsd")) {
                    Schema schema = xsdRepository.getByName(file);
                    ValidationError error = xsdValidator.validate(body, schema);
                    if (error != null) {
                        cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Ignoring unknown rule format: " + file + " when validating file " + cm.getFileName() + ", supported formats are 'xsl' and 'xsd'");
                }
            }
        }

        return cm;
    }

    @NotNull
    private ContainerMessage executeXsl(@NotNull byte[] body, @NotNull ContainerMessage cm, @NotNull Templates template, @NotNull ValidationRule rule,
                                        @NotNull String file, @NotNull Endpoint endpoint)
            throws TransformerException, IOException, SAXException, ParserConfigurationException {
        try {
            return resultParser.parse(cm, xslValidator.validate(body, template).getInputStream(), rule);
        } catch (XPathException e) {
            ValidationError validationError = new ValidationError("XSL parser failure")
                    .withLocation(e.getLocator() == null ? "Undefined location" : e.getLocator().toString())
                    .withText(e.getMessage())
                    .withIdentifier(e.getErrorCodeQName() == null ? "err" : e.getErrorCodeQName().toString())
                    .withFlag("fatal")
                    .withTest("XSL validation: " + file);
            cm.addError(validationError.toDocumentError(endpoint));
            return cm;
        }
    }

}