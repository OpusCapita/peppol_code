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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
//@Component
//@Lazy
public class SvefakturaOneValidator {
    private final static Logger logger = LoggerFactory.getLogger(SvefakturaOneValidator.class);

    @Value("${peppol.validator.svefaktura1.xsd.path}")
    private String xsdPath;
    @Value("${peppol.validator.svefaktura1.schematron.enabled}")
    private boolean xslEnabled;
    @Value("${peppol.validator.svefaktura1.schematron.path}")
    private String xslPath;

    private final XsdValidator xsdValidator;
    private final XsdRepository xsdRepository;
    private final XslRepository xslRepository;
    private final XslValidator xslValidator;
    private final ResultParser resultParser;

    @Autowired
    public SvefakturaOneValidator(@NotNull XsdValidator xsdValidator, @NotNull XsdRepository xsdRepository, @NotNull XslRepository xslRepository,
                                  @NotNull XslValidator xslValidator, @NotNull ResultParser resultParser) {
        this.xsdValidator = xsdValidator;
        this.xsdRepository = xsdRepository;
        this.xslRepository = xslRepository;
        this.xslValidator = xslValidator;
        this.resultParser = resultParser;
    }

    @NotNull
    public ContainerMessage validate(@NotNull byte[] documentBody, @NotNull ContainerMessage cm) throws TransformerException, ParserConfigurationException, SAXException, IOException {
        cm = validateXsd(documentBody, cm);
        if (xslEnabled) {
            cm = validateXsl(documentBody, cm);
        }
        return cm;
    }

    @SuppressWarnings("ConstantConditions")
    private ContainerMessage validateXsd(byte[] body, ContainerMessage cm) {
        logger.info("Validating Svefaktura1 " + cm.getFileName() + " against XSD");
        ValidationError error = xsdValidator.validate(body, xsdRepository.getByName(xsdPath));
        if (error != null) {
            logger.info("XSD validation failed for Svefaktura1 " + cm.getFileName());
            cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
        }
        return cm;
    }

    private ContainerMessage validateXsl(byte[] body, ContainerMessage cm) throws TransformerException, IOException, SAXException, ParserConfigurationException {
        logger.info("Validating Svefaktura1 " + cm.getFileName() + " against XSL");
        Templates template = xslRepository.getByFileName(xslPath);
        FastByteArrayOutputStream out = xslValidator.validate(body, template);
        return resultParser.parse(cm, out.getInputStream());
    }
}
