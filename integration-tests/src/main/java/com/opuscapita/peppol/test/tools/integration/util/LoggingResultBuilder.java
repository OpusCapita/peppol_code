package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.01..
 */
public class LoggingResultBuilder implements ResultBuilder {

    private final static Logger logger = LogManager.getLogger(LoggingResultBuilder.class);

    @Override
    public void processResult(List<TestResult> checkResults) {
        logger.info("LoggingResultBuilder start *****************************************");
        if(checkResults == null  || checkResults.isEmpty())
            logger.info("LoggingResultBuilder: empty test results! ");
        for (TestResult result : checkResults) {
            if (!result.isPassed())
                logger.warn(result.toString());
            else
                logger.info(result.toString());
        }
        logger.info("LoggingResultBuilder end *****************************************");
    }
}
