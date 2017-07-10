package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class FileSystemCheck extends Check {

    public FileSystemCheck(String moduleName, Map<String, Object> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        try {
            boolean canWrite = true, canRead = true;
            List<String> writableDirectories = ((List<String>)rawConfig.get("writable-directories"));
            List<String> readableDirectories = ((List<String>)rawConfig.get("readable-directories"));

            for (String dir: writableDirectories) {
                if (!Files.isWritable(new File(dir).toPath()))
                    canWrite = false;
            }
             for (String dir : readableDirectories){
                 if (!Files.isReadable(new File(dir).toPath()))
                     canRead = false;
             }

            return new CheckResult(moduleName, canWrite && canRead, "read/write check for directories: " +
                    rawConfig.get("readable-directories") + " and " +
                    rawConfig.get("writable-directories"), rawConfig);
        } catch (Exception ex){
            ex.printStackTrace();
            return new CheckResult(moduleName, false, "read/write check for directories: " +
                    rawConfig.get("readable-directories") + " and " +
                    rawConfig.get("writable-directories") + " failed! " + ex, rawConfig);
        }
    }
}
