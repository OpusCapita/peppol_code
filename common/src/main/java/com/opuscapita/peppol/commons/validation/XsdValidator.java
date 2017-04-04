package com.opuscapita.peppol.commons.validation;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by bambr on 16.3.10.
 */
public interface XsdValidator {
    List<ValidationError> performXsdValidation(@NotNull ContainerMessage containerMessage, @NotNull Document dom);

    default void validateAgainstXsd(ContainerMessage containerMessage, String xsdPath) throws Exception {
        byte[] data = Files.readAllBytes(Paths.get(containerMessage.getFileName()));
        validateAgainstXsd(data, xsdPath);
    }

    default void validateAgainstXsd(byte[] data, String xsdPath) throws SAXException, TransformerException, IOException {
        File xsd = new File(xsdPath);
        if (!xsd.exists() || !xsd.canRead()) {
            throw new IOException("SBDH XSD File not found or can not be read.");
        }
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(xsd.toURI().toURL());
        javax.xml.validation.Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new ByteArrayInputStream(data)));
    }
}
