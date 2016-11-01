package com.opuscapita.peppol.commons.container;

import com.google.gson.Gson;
import com.opuscapita.peppol.commons.container.document.BaseDocument;
import com.opuscapita.peppol.commons.container.route.Endpoint;
import com.opuscapita.peppol.commons.container.route.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Properties;

/**
 * Holds the whole data exchange bean inside the application.
 *
 * @author Sergejs.Roze
 */
@SuppressWarnings("WeakerAccess")
public class ContainerMessage implements Serializable {
    public static final String SOURCE_INFO = "source_info";

    private final BaseDocument document;
    private final Endpoint source;
    private final String fileName;
    private final Route route;
    private final Properties metadata = new Properties();
    private String transactionId;

    public ContainerMessage(@NotNull String metadata, @NotNull String fileName, @NotNull Endpoint source) {
        this(null, metadata, source, fileName, null);
    }

    public ContainerMessage(@NotNull BaseDocument document, @NotNull String fileName, @NotNull Endpoint source) {
        this(document, fileName, source, fileName, null);
    }

    public ContainerMessage(@Nullable BaseDocument document, @Nullable String metadata, @NotNull Endpoint source,
                            @NotNull String fileName, @Nullable Route route) {
        this.document = document;
        this.metadata.put(SOURCE_INFO, metadata == null ? "" : metadata);
        this.source = source;
        this.fileName = fileName;
        this.route = route;
    }

    @Nullable
    public Route getRoute() {
        return route;
    }

    @Nullable
    public BaseDocument getBaseDocument() {
        return document;
    }

    public boolean isInbound() {
        return source == Endpoint.PEPPOL;
    }

    @NotNull
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns customer ID depending on the direction of the message, either sender or recipient ID.
     */
    @Nullable
    public String getCustomerId() {
        if (getBaseDocument() == null) {
            return null;
        }
        return isInbound() ? getBaseDocument().getRecipientId() : getBaseDocument().getSenderId();
    }

    @Override
    public String toString() {
        if (document == null) {
            return fileName + " from " + source;
        }
        return document.getClass().getName() + " [" + route + "]";
    }

    @Nullable
    public String getSourceMetadata() {
        return metadata.getProperty(SOURCE_INFO);
    }

    @NotNull
    public Properties getMetadata() {
        return metadata;
    }

    public Endpoint getSource() {
        return source;
    }

    public byte[] getBytes() {
        return new Gson().toJson(this).getBytes();
    }

    @Nullable
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(@NotNull String transactionId) {
        this.transactionId = transactionId;
    }
}
