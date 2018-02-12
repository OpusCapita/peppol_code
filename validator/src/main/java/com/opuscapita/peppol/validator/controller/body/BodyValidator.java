package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Validates document body using either XSLT or XSD depending on the document type.
 *
 * @author Sergejs.Roze
 */
@Component
public class BodyValidator {
    private final ValidationRuleExecutor validationRuleExecutor;

    @Autowired
    public BodyValidator(@NotNull ValidationRuleExecutor validationRuleExecutor) {
        this.validationRuleExecutor = validationRuleExecutor;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] documentBody, @NotNull ContainerMessage cm)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        return validationRuleExecutor.validate(documentBody, cm);
    }

}
