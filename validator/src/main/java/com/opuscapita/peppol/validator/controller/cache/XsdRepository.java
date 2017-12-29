package com.opuscapita.peppol.validator.controller.cache;

import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import java.io.IOException;

/**
 * @author Sergejs.Roze
 */
public interface XsdRepository {

    Schema getByName(@NotNull String name) throws SAXException, IOException;
}
