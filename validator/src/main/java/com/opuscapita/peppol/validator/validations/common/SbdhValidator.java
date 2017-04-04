package com.opuscapita.peppol.validator.validations.common;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.document.DocumentUtils;
import com.opuscapita.peppol.commons.validation.ValidationError;
import com.opuscapita.peppol.commons.validation.XsdValidator;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bambr on 16.3.10.
 */
@Component
public class SbdhValidator implements XsdValidator {
    @Value("${peppol.validator.sbdh.xsd}")
    String xsdPath;

    @Override
    public List<ValidationError> performXsdValidation(@NotNull ContainerMessage containerMessage, @NotNull Document dom) {
        Node rootNode = DocumentUtils.getRootNode(dom);
        if (rootNode == null) {
            throw new IllegalArgumentException("Failed to locate root node in document: " + containerMessage.getFileName());
        }

        String contentRootNode = rootNode.getNodeName();
        try {
            validateAgainstXsd(containerMessage, xsdPath);
        } catch (Exception e) {
            if (!(e.getMessage().contains("Cannot find the declaration of element '" + contentRootNode + "'") || (e.getMessage().contains("Invalid content was found starting with element '" + contentRootNode + "'")))) {
                return new ArrayList<ValidationError>() {{
                    add(new ValidationError("SBDH validation failure").withText(e.getMessage()));
                }};
            }
        }
        return Collections.emptyList();
    }


}
