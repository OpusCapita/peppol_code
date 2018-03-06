package com.opuscapita.peppol.test.tools.integration.util;

import com.opuscapita.peppol.test.tools.integration.test.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by gamanse1 on 2016.11.01..
 */
public class LoggingResultBuilder implements ResultBuilder {

    private final static Logger logger = LoggerFactory.getLogger(LoggingResultBuilder.class);

    @Override
    public void processResult(List<TestResult> testResults) {
        logger.info("LoggingResultBuilder start *****************************************");
        int success = 0, failed = 0;
        if (testResults == null || testResults.isEmpty()) {
            logger.error("LoggingResultBuilder: empty test results");
            logger.info("LoggingResultBuilder end *****************************************");
            return;
        }

        for (TestResult result : testResults) {
            if (!result.isPassed()) {
                logger.error(result.toString());
                failed++;
            } else {
                logger.info(result.toString());
                success++;
            }
        }
        logger.info("Total tests : " + testResults.size() + " success: " + success + " failed: " + failed);
        logger.info("LoggingResultBuilder end *****************************************");
    }
}
