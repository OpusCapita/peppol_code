package com.opuscapita.peppol.inbound;

import eu.peppol.PeppolMessageMetaData;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import eu.peppol.util.OxalisVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Copied from SimpleMessageRepository of Oxalis to prevent IllegalAccessError.
 *
 * @author Sergejs.Roze
 */
public class MetadataUtils {

    @SuppressWarnings("StringBufferReplaceableByString")
    @NotNull
    public static String getHeadersAsJson(@NotNull PeppolMessageMetaData headers) {
        try {
            StringBuilder ex = new StringBuilder();
            ex.append("{ \"PeppolMessageMetaData\" :\n  {\n");
            ex.append(createJsonPair("messageId", headers.getMessageId()));
            ex.append(createJsonPair("recipientId", headers.getRecipientId()));
            ex.append(createJsonPair("recipientSchemeId", getSchemeId(headers.getRecipientId())));
            ex.append(createJsonPair("senderId", headers.getSenderId()));
            ex.append(createJsonPair("senderSchemeId", getSchemeId(headers.getSenderId())));
            ex.append(createJsonPair("documentTypeIdentifier", headers.getDocumentTypeIdentifier()));
            ex.append(createJsonPair("profileTypeIdentifier", headers.getProfileTypeIdentifier()));
            ex.append(createJsonPair("sendingAccessPoint", headers.getSendingAccessPoint()));
            ex.append(createJsonPair("receivingAccessPoint", headers.getReceivingAccessPoint()));
            ex.append(createJsonPair("protocol", headers.getProtocol()));
            ex.append(createJsonPair("userAgent", headers.getUserAgent()));
            ex.append(createJsonPair("userAgentVersion", headers.getUserAgentVersion()));
            ex.append(createJsonPair("sendersTimeStamp", headers.getSendersTimeStamp()));
            ex.append(createJsonPair("receivedTimeStamp", headers.getReceivedTimeStamp()));
            ex.append(createJsonPair("sendingAccessPointPrincipal",
                    headers.getSendingAccessPointPrincipal() == null ? null : headers.getSendingAccessPointPrincipal().getName()));
            ex.append(createJsonPair("transmissionId", headers.getTransmissionId()));
            ex.append(createJsonPair("buildUser", OxalisVersion.getUser()));
            ex.append(createJsonPair("buildDescription", OxalisVersion.getBuildDescription()));
            ex.append(createJsonPair("buildTimeStamp", OxalisVersion.getBuildTimeStamp()));
            ex.append("    \"oxalis\" : \"").append(OxalisVersion.getVersion()).append("\"\n");
            ex.append("  }\n}\n");
            return ex.toString();
        } catch (Exception e) {
            return headers.toString();
        }
    }

    private static String getSchemeId(ParticipantId participant) {
        String id = "UNKNOWN:SCHEME";
        if(participant != null) {
            String prefix = participant.stringValue().split(":")[0];
            SchemeId scheme = SchemeId.fromISO6523(prefix);
            if(scheme != null) {
                id = scheme.getSchemeId();
            } else {
                id = "UNKNOWN:" + prefix;
            }
        }

        return id;
    }

    private static String createJsonPair(String key, Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append("    \"").append(key).append("\" : ");
        if(value == null) {
            sb.append("null,\n");
        } else {
            sb.append("\"").append(value.toString()).append("\",\n");
        }

        return sb.toString();
    }

}
