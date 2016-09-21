package com.opuscapita.peppol.support.ui.components;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

import static java.nio.file.Files.readAllBytes;

/**
 * Created by KACENAR1 on 22.08.2014
 */
public class PidUtil {
    public static final long PID_FILE_INTERVAL = 60000L; // 1 minute
    private static final Logger logger = Logger.getLogger(PidUtil.class);

    public static String getProcessPid() {
        String result = "";
        if (!checkPidFile()) {
            return result;
        }
        try {
            result = new String(readAllBytes(getPidPath()));
        } catch (Exception e) {

        }
        return result;
    }

    public static boolean checkPidFile() {
        final String homeDir = System.getProperty("user.home");
        if (homeDir == null || homeDir.isEmpty()) {
            logger.error("USER.HOME is not defined. Please set USER.HOME to execute application.");
            return false;
        }
        final Path peppolDir = Paths.get(homeDir, File.separator, ".peppol");
        if (!Files.isDirectory(peppolDir)) {
            try {
                Files.createDirectory(peppolDir);
            } catch (IOException e) {
                logger.error("Unable to create: " + peppolDir);
                return false;
            }
        }
        final Path peppolPid = getPidPath();
        if (Files.exists(peppolPid)) {
            try {
                FileTime modifiedAt = Files.getLastModifiedTime(peppolPid);
                if (Calendar.getInstance().getTimeInMillis() - modifiedAt.toMillis() < PID_FILE_INTERVAL) {
                    logger.error("PID file already exists. Terminate previous process before starting new or remove: " + peppolPid);
                    return false;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public static boolean updatePidFile(Path peppolPid) {
        try {
            Files.write(peppolPid, ManagementFactory.getRuntimeMXBean().getName().split("@")[0].getBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            logger.error("Cannot create file " + peppolPid + ". Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static Path getPidPath() {
        return Paths.get(System.getProperty("user.home"), File.separator, ".peppol", File.separator, "peppol-ap.pid");
    }

    public static void shutdownHook() {
        Path pepolPid = getPidPath();
        try {
            Files.deleteIfExists(pepolPid);
            logger.info("PID file deleted.");
        } catch (IOException e) {
            logger.error("Failed to delete PID file!");
        }
    }
}
