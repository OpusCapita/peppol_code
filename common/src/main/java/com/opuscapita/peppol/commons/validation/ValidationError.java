package com.opuscapita.peppol.commons.validation;

import java.io.Serializable;

/**
 * Created by Daniil on 03.05.2016.
 */
public class ValidationError implements Serializable {
    private String title;
    private String identifier;
    private String location;
    private String flag;
    private String text;
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
        return this;
    }

    public String getText() {
        return text;
    }

    public ValidationError withTest(String test) {
        this.test = test;
        return this;
    }

    public String getTest() {
        return test;
    }

    @Override
    public String toString() {
        String result = "";

        result += (test == null) ? "" : test.trim();
        result += (flag == null) ? "" : " [" + flag + "] ";
        result += (location == null) ? "" : " at " + location;
        result += (identifier == null) ? "" : " (" + identifier.trim() + ")";
        result += (text == null) ? "" : "\n" + text.trim();
        return result;
    }

}
