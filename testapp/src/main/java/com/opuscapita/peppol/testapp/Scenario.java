package com.opuscapita.peppol.testapp;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Single test script to be executed.
 *
 * @author Sergejs.Roze
 */
public class Scenario implements AutoCloseable {
    private String script;
    private String name;

    public Scenario() {
        this(null, null);
    }

    public Scenario(@Nullable String script) {
        this(script, null);
    }

    public Scenario(@Nullable String script, @Nullable String name) {
        this.script = script;
        this.name = name;
    }

    @Override
    public void close() throws Exception {

    }

    @Nullable
    public String getScript() {
        return script;
    }

    public Scenario load(@NotNull String fileName) throws IOException {
        load(new File(fileName));
        this.name = fileName;
        return this;
    }

    public Scenario load(@NotNull File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            load(inputStream);
        }
        this.name = file.getName();
        return this;
    }

    public Scenario load(@NotNull InputStream inputStream) throws IOException {
        script = new String(IOUtils.toByteArray(inputStream));
        return this;
    }

    public String getName() {
        return name;
    }
}
