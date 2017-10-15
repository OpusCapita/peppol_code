package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * @author Sergejs.Roze
 */
@Component
public class XslRepositoryImpl implements XslRepository {
    @Value("${peppol.validator.xsl.directory}")
    private String templatesDirectory;

    private final TransformerFactory transformerFactory;

    @Autowired
    public XslRepositoryImpl(@NotNull @Lazy TransformerFactory transformerFactory) {
        this.transformerFactory = transformerFactory;
    }

    @Override
    @Cacheable("xsl")
    public Templates getByName(@NotNull String name) throws TransformerConfigurationException {
        return loadXsl(name);
    }

    private Templates loadXsl(String name) throws TransformerConfigurationException {
        File sourceFile = new File(templatesDirectory, name);
        Source xsltSource = new StreamSource(sourceFile);
        return transformerFactory.newTemplates(xsltSource);
    }

}
