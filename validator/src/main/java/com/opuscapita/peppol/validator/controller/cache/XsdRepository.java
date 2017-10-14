package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;

import javax.xml.validation.Schema;

/**
 * @author Sergejs.Roze
 */
public interface XsdRepository {

    Schema getByName(@NotNull String name);
}
