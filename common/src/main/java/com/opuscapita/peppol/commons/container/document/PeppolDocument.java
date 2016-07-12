package com.opuscapita.peppol.commons.container.document;

/**
 * @author Sergejs.Roze
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PeppolDocument {
    /**
     * Human readable description of the Peppol document format.
     */
    String value();
}
