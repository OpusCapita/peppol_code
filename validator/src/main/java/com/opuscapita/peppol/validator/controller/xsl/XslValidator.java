package com.opuscapita.peppol.validator.controller.xsl;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.FastByteArrayOutputStream;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

/**
 * @author Sergejs.Roze
 */
//@Component
public class XslValidator {

    @NotNull
    public FastByteArrayOutputStream validate(@NotNull byte[] data, @NotNull Templates template) throws TransformerException {
        Transformer transformer = template.newTransformer();

        Source source = new StreamSource(new StringReader(new String(data)));
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        Result result = new StreamResult(outputStream);

        transformer.transform(source, result);

        return outputStream;
    }

}
