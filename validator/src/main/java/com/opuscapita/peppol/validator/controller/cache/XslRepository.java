package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
public interface XslRepository {

    Templates getByFileName(@NotNull String name) throws TransformerConfigurationException, IOException;

}
