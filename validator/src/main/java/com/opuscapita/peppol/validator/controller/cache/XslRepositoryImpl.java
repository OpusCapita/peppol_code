package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
@Component
public class XslRepositoryImpl implements XslRepository {
    private final TransformerFactory transformerFactory;
    private final FileSource fileSource;

    @Autowired
    public XslRepositoryImpl(@NotNull @Lazy TransformerFactory transformerFactory, @NotNull FileSource fileSource) {
        this.transformerFactory = transformerFactory;
        this.fileSource = fileSource;
    }

    @Override
    @Cacheable("xsl")
    public Templates getByFileName(@NotNull String name) throws TransformerConfigurationException, IOException {
        return loadXsl(name);
    }

    private Templates loadXsl(String name) throws TransformerConfigurationException, IOException {
        File sourceFile = fileSource.getFile(name);
        Source xsltSource = new StreamSource(sourceFile);
        return transformerFactory.newTemplates(xsltSource);
    }

}
