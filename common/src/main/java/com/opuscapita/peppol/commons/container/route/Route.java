package com.opuscapita.peppol.commons.container.route;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * The complete route from end-to-end with error handling.
 *
 * @author Sergejs.Roze
 */
public class Route {
    private Process current;

    private HashMap<String, Process> processes;

    /**
     * Returns next process in the route depending on the result
     * and updates route that it points to the next process which becomes current.
     *
     * @param status the outcome of the finished process
     * @return the next process if any or null when this was the end process
     */
    @Nullable
    public Process pop(Status status) {
        if (current == null) {
            throw new IllegalStateException("The end of route already reached");
        }

        current.setStatus(status);
        Process next;
        switch (status) {
            case OK:
                next = processes.get(current.getOk());
                break;
            case NOK:
                next = processes.get(current.getNok());
                break;
            case ERROR:
                next = processes.get(current.getError());
                break;
            default:
                throw new IllegalArgumentException("Cannot set undefined status for finished process");
        }

        current = next;
        return next;
    }

    public Route load(String fileName) throws IOException {
        return load(new File(fileName));
    }

    public Route load(File file) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return load(inputStream);
        }
    }

    public Route load(InputStream inputStream) {
        return null;
    }

}
