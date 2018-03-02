package com.opuscapita.peppol.validator.controller.xsd;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.cache.XsdRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class HeaderValidator {
    private final static Logger logger = LoggerFactory.getLogger(HeaderValidator.class);

    private final XsdValidator xsdValidator;
    private final XsdRepository xsdRepository;

    @Value("${peppol.validator.rules.directory}")
    private String rulesDirectory;
    @Value("${peppol.validator.sbdh.xsdplus}")
    private String xsdPath;

    @Autowired
    public HeaderValidator(@NotNull XsdValidator xsdValidator, @NotNull XsdRepository xsdRepository) {
        this.xsdValidator = xsdValidator;
        this.xsdRepository = xsdRepository;
    }

    @PostConstruct
    private void checkValues() {
        File file = new File(rulesDirectory, xsdPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Required file not found: " + file.getAbsolutePath());
        }
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] data, @NotNull ContainerMessage cm) throws SAXException, IOException {
        logger.debug("Checking header vs XSD: " + xsdPath);
        if (data.length == 0) {
            return cm;
        }
        Schema schema = xsdRepository.getByName(xsdPath);
        ValidationError error = xsdValidator.validate(data, schema);

        if (error != null) {
            cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
        }
        return cm;
    }

}
