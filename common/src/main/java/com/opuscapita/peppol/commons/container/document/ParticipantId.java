package com.opuscapita.peppol.commons.container.document;

/**
 * Created by bambr on 17.21.2.
 */
public class ParticipantId {
    String rawValue;
    String prefix;
    String participantId;

    public ParticipantId(String rawValue) {
        this.rawValue = rawValue;
        if (rawValue.contains(":")) {
            String[] parts = rawValue.split(":", 2);
            prefix = parts[0];
            participantId = parts[1];
        } else {
            prefix = null;
            participantId = rawValue;
        }
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParticipantId that = (ParticipantId) o;

        if (prefix != null && that.prefix != null) {
            if (!prefix.equals(that.prefix)) {
                return false;
            }
        }
        return participantId != null ? participantId.equals(that.participantId) : that.participantId == null;
    }

    @Override
    public int hashCode() {
        int result = rawValue.hashCode();
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        result = 31 * result + (participantId != null ? participantId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return rawValue;
    }
}
