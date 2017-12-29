package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class XsdRepositoryImpl implements XsdRepository {
    private final FileSource fileSource;
    private final SchemaFactory schemaFactory;

    @Autowired
    public XsdRepositoryImpl(@NotNull FileSource fileSource, @NotNull SchemaFactory schemaFactory) {
        this.fileSource = fileSource;
        this.schemaFactory = schemaFactory;
    }

    @Override
    @Cacheable("xsd")
    public Schema getByName(@NotNull String name) throws SAXException, IOException {
        return loadXsd(name);
    }

    private Schema loadXsd(String name) throws SAXException, IOException {
        return schemaFactory.newSchema(fileSource.getFile(name));
    }

}
