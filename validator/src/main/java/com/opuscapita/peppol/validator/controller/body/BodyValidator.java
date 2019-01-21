package com.opuscapita.peppol.validator.controller.body;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

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
    public ContainerMessage validate(@NotNull byte[] documentBody, @NotNull ContainerMessage cm, @NotNull Endpoint endpoint)
            throws IOException, ParserConfigurationException, SAXException, TransformerException, TimeoutException {
        return validationRuleExecutor.validate(documentBody, cm, endpoint);
    }

}
