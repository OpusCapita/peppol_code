package com.opuscapita.peppol.test.tools.integration.consumers.subtypes;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.opuscapita.peppol.test.tools.integration.test.TestResult;

public class MlrConsumer extends FileConsumer {

    private final String ERROR_DESCRIPTION = "<cbc:Description>This sending expected to fail I/O in test mode</cbc:Description>";
    public MlrConsumer(String id, String fileTestName, String expectedValue) {
        super(id, fileTestName, expectedValue);
    }

    @Override
    public TestResult consume(Object consumable) {
        init(consumable);
        if(!file.exists())
            waitFixedDealy();
        try {
            if (file.exists()) {
                String content = Files.toString(file, Charsets.UTF_8);
                if(content.contains(ERROR_DESCRIPTION))
                    result = new TestResult(name, true, "IO exception found in " + file.getAbsolutePath());
                else
                    result = new TestResult(name, false, "IO exception not found in " + file.getAbsolutePath());
                clean();
                return result;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}
