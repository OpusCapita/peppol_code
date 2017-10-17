package com.opuscapita.peppol.validator.controller.xsd;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.validator.controller.cache.XsdRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.validation.Schema;

/**
 * @author Sergejs.Roze
 */
//@Component
public class HeaderValidator {
    private final XsdValidator xsdValidator;
    private final XsdRepository xsdRepository;

    @Value("${peppol.validator.sbdh.xsd}")
    String xsdPath;

    @Autowired
    public HeaderValidator(@NotNull XsdValidator xsdValidator, @NotNull XsdRepository xsdRepository) {
        this.xsdValidator = xsdValidator;
        this.xsdRepository = xsdRepository;
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ContainerMessage validate(@NotNull byte[] data, @NotNull ContainerMessage cm) {
        Schema schema = xsdRepository.getByName(xsdPath);
        ValidationError error = xsdValidator.validate(data, schema);

        if (error != null) {
            cm.addError(error.toDocumentError(cm.getProcessingInfo().getCurrentEndpoint()));
        }
        return cm;
    }

}
