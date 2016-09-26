package com.opuscapita.peppol.support.ui.components;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;

/**
 * Created by KACENAR1 on 22.08.2014
 */
public class QuartzStatusUtil {

    public static Path getQuartzStatusPath() {
        return Paths.get(System.getProperty("user.home"), File.separator, ".peppol", File.separator, "quart-status.txt");
    }

    public static String getQuartzStatus() {
        String result = "";
        try {
            result = new String(readAllBytes(getQuartzStatusPath()));
        } catch (Exception e) {

        }
        return result;
    }
}
