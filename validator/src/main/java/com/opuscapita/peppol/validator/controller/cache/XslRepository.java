package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

/**
 * @author Sergejs.Roze
 */
public interface XslRepository {

    Templates getByName(@NotNull String name) throws TransformerConfigurationException;

}
