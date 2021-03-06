package com.opuscapita.peppol.commons.validation;

import com.google.gson.annotations.Since;
import com.opuscapita.peppol.commons.container.document.DocumentError;
import com.opuscapita.peppol.commons.container.document.DocumentWarning;
import com.opuscapita.peppol.commons.container.process.route.Endpoint;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Daniil on 03.05.2016.
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
public class ValidationError implements Serializable {
    private static final long serialVersionUID = -5071816172421440591L;

    @Since(1.0)
    private String title;
    @Since(1.0)
    private String identifier;
    @Since(1.0)
    private String location;
    @Since(1.0)
    private String flag;
    @Since(1.0)
    private String text;
    @Since(1.0)
    private String test;

    public ValidationError() {}

    public ValidationError(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public ValidationError withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDetails() {
        return toString();
    }

    public ValidationError withIdentifier(String identifier) {
        this.identifier = identifier;
        this.identifier = extractIdentifierFromText();
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ValidationError withLocation(String location) {
        this.location = location;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public ValidationError withFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getFlag() {
        return flag;
    }

    public ValidationError withText(String text) {
        this.text = text;
        this.identifier = extractIdentifierFromText();
        return this;
    }

    public String getText() {
        return text;
    }

    public ValidationError withTest(String test) {
        this.test = test;
        return this;
    }

    private String extractIdentifierFromText() {
        if (StringUtils.isNotBlank(this.identifier) && !this.identifier.equals("N/A")) {
            return this.identifier;
        }

        if (StringUtils.isNotBlank(this.text)) {
            Matcher m = Pattern.compile("\\[([^)]+)\\]").matcher(this.text);
            if (m.find()) {
                return m.group(1);
            }
        }

        return this.identifier;
    }

    public String getTest() {
        return test;
    }

    @NotNull
    public DocumentError toDocumentError(@NotNull Endpoint source) {
        return new DocumentError(source, this.toString(), this);
    }

    @NotNull
    public DocumentWarning toDocumentWarning(@NotNull Endpoint source) {
        return new DocumentWarning(source, this.toString(), this);
    }

    @Override
    public String toString() {
        String result = "";

        result += (test == null) ? "" : test.trim();
        result += (flag == null) ? "" : " [" + flag + "] ";
        result += (location == null) ? "" : " at " + location;
        result += (identifier == null) ? "" : " (" + identifier.trim() + ")";
        result += (text == null) ? "" : "; " + text.trim();
        return result;
    }

}
