package com.opuscapita.peppol.commons.errors.oxalis;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Sergejs.Roze
 */
@Component
public class OxalisErrorRecognizer {
    private final static Logger logger = LoggerFactory.getLogger(OxalisErrorRecognizer.class);
    private final OxalisErrorsList oxalisErrorsList;

    @Autowired
    public OxalisErrorRecognizer(@NotNull OxalisErrorsList oxalisErrorsList) {
        this.oxalisErrorsList = oxalisErrorsList;
    }

    @NotNull
    public SendingErrors recognize(@NotNull Throwable e) {
        if (e.getMessage() == null) {
            return SendingErrors.OTHER_ERROR;
        }
        return recognize(e.getMessage());
    }

    @NotNull
    public SendingErrors recognize(@NotNull String message) {
        if (oxalisErrorsList.getList() != null) {
            for (OxalisError known : oxalisErrorsList.getList()) {
                logger.debug("Trying " + known.getMask() + " as " + known.getType());
                if (StringUtils.isNotBlank(known.getMask())) {
                    if (message.replaceAll("\n", " ").replaceAll("\r", " ").matches(known.getMask())) {
                        logger.debug("Exception message '" + StringUtils.substring(message, 0, 64) +
                                "...' recognized as " + known.getType());
                        return known.getType();
                    }
                }
            }
        }

        logger.warn("Failed to recognize error message: " + StringUtils.substring(message, 0, 64));
        return SendingErrors.OTHER_ERROR;
    }
}
