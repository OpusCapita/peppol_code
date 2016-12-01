package com.opuscapita.peppol.transport.controller;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class TemplateTools {
    public static final String BEGIN_END = "%";
    public static final String T_FILENAME = "FILENAME";
    public static final String T_CURRENT_DATE = "CURRENT_DATE";
    public static final String T_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String T_PERCENT = "PERCENT";
    public static final String T_ARCHETYPE = "ARCHETYPE";
    public static final String T_DOCUMENT_TYPE = "DOCUMENT_TYPE";
    private static final Logger logger = LoggerFactory.getLogger(TemplateTools.class);

    @NotNull
    public static String templateToPath(@NotNull String template, @NotNull ContainerMessage cm) {
        String[] tokens = template.split(BEGIN_END);

        String result = "";
        boolean odd = false;
        for (String token : tokens) {
            if (odd) {
                switch (token) {
                    case T_FILENAME:
                        result += FilenameUtils.getName(cm.getFileName());
                        break;
                    case T_CURRENT_DATE:
                        result += new SimpleDateFormat("yyyyMMdd").format(new Date());
                        break;
                    case T_CUSTOMER_ID:
                        result += cm.getCustomerId();
                        break;
                    case T_PERCENT:
                        result += "%";
                        break;
                    case T_ARCHETYPE:
                        result += cm.getBaseDocument() == null ? "NA" : cm.getBaseDocument().getArchetype();
                        break;
                    case T_DOCUMENT_TYPE:
                        result += cm.getBaseDocument() == null ? "NA" : cm.getBaseDocument().getDocumentType();
                        break;
                    default:
                        logger.error("Unknown template: " + BEGIN_END + token + BEGIN_END);
                }
            } else {
                result += token;
            }
            odd = !odd;
        }

        return result;
    }


}
