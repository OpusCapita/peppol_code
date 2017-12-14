package com.opuscapita.peppol.outbound;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.errors.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.StringJoiner;

/**
 * @author Sergejs.Roze
 */
@Component
public class OutboundExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(OutboundExceptionHandler.class);

    private final ErrorHandler errorHandler;

    public OutboundExceptionHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void handle(Throwable e, Method method, Object... something) {
        if (something.length == 1 && something[0] instanceof ContainerMessage) {
            ContainerMessage cm = (ContainerMessage) something[0];
            errorHandler.reportWithContainerMessage(cm, e, "Outbound sending failed");
        } else {
            logger.error("Expected call to send(ContainerMessage) but got call to " + method.getName() + "(" + somethingToString(something) + ")");
            errorHandler.reportWithoutContainerMessage(null, e, "Outbound sending failed", null, null,
                    "Method: " + method.getName() + "(" + somethingToString(something) + ")");
        }
    }

    private String somethingToString(Object... something) {
        StringJoiner sj = new StringJoiner(", ");
        for (Object o : something) {
            sj.add(o.toString());
        }
        return sj.toString();
    }
}
