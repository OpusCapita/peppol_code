package com.opuscapita.peppol.test.tools.smoke.checks.subtypes;

import com.opuscapita.peppol.test.tools.smoke.checks.Check;
import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

/**
 * Created by bambr on 16.20.10.
 */
public class FileSystemCheck extends Check {

    public FileSystemCheck(String moduleName, Map<String, String> params) {
        super(moduleName, params);
    }

    @Override
    public CheckResult run() {
        CheckResult checkResult = null;
        try {
            boolean canWrite = Files.isWritable(new File(rawConfig.get("writable-directory")).toPath());
            boolean canRead = Files.isReadable(new File(rawConfig.get("readable-directory")).toPath());
            checkResult = new CheckResult(name, canWrite && canRead, "read/write test for directories: " +
                    rawConfig.get("readable-directory") + " and " +
                    rawConfig.get("writable-directory"), rawConfig);
        }
        catch (Exception ex){
            ex.printStackTrace();
            checkResult = new CheckResult(name, false, "read/write test for directories: " +
                    rawConfig.get("readable-directory") + " and " +
                    rawConfig.get("writable-directory") + " failed! " + ex, rawConfig);
        }
        return checkResult;
    }
}
