package com.opuscapita.peppol.commons.container.document;

/**
 * Created by bambr on 17.22.2.
 */
public class SbdhDocumentConsistencyChecker {
//    private static final Logger logger = LoggerFactory.getLogger(SbdhDocumentConsistencyChecker.class);
//
//    public static boolean doTheCheck(String value, String sbdhValue, String fieldName, DocumentInfo base, BiConsumer<String, DocumentInfo> fieldSetter) {
//        boolean result = true;
//        Node sbdh = base.getSbdhNode();
//        if (value == null && sbdhValue == null) {
//            logger.warn("Failed to find " + fieldName + " field");
//            result = false;
//        } else if (sbdh != null && sbdhValue != null) {
//            ParticipantId sbdhSender = new ParticipantId(sbdhValue);
//            ParticipantId documentSender = new ParticipantId(value);
//            if (!sbdhSender.equals(documentSender)) {
//                logger.warn("SBDH and document have different value for " + fieldName + ": " + sbdhValue + " <> " + value);
//                result = false;
//            } else {
//                fieldSetter.accept(sbdhValue != null ? sbdhValue : value, base);
//            }
//        } else {
//            fieldSetter.accept(value, base);
//        }
//        return result;
//    }
}
