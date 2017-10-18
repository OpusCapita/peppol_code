package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

import javax.xml.validation.Schema;

/**
 * @author Sergejs.Roze
 */
//@Component
public class XsdRepositoryImpl implements XsdRepository {
    @Value("${peppol.validator.xsd.directory}")
    private String templatesDirectory;

    @Override
    @Cacheable("xsd")
    public Schema getByName(@NotNull String name) {
        return null;
    }

}
