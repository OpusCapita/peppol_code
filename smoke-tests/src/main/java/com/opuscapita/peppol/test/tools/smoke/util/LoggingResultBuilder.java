package com.opuscapita.peppol.test.tools.smoke.util;

import com.opuscapita.peppol.test.tools.smoke.checks.CheckResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.01..
 */
public class LoggingResultBuilder implements ResultBuilder {

    private final static Logger logger = LogManager.getLogger(LoggingResultBuilder.class);

    @Override
    public void processResult(List<CheckResult> checkResults) {
        for (CheckResult result : checkResults) {
            if (!result.isPassed())
                logger.warn(result.toString());
            else
                logger.info(result.toString());
        }
    }
}
